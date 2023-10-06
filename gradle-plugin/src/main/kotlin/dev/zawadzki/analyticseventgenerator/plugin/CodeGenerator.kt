package dev.zawadzki.analyticseventgenerator.plugin

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.buildCodeBlock
import dev.zawadzki.analyticseventgenerator.runtime.AbstractEvent

internal data class CodeGenerationParams(
    val prefix: String,
    val packageName: String
)

internal fun generateCode(params: CodeGenerationParams, document: Document): List<FileSpec> {
    // event value allowlist
    // attribute value allowlist

    val files = document.events.map { event ->
        val eventName = params.prefix + event.name.toClassName()
        val classBuilder = TypeSpec.classBuilder(eventName)
            .superclass(AbstractEvent::class)
        val constructorBuilder = FunSpec.constructorBuilder()

        val attributesPropertyValues = mutableListOf<String>()

        for (attribute in event.attributes) {
            val attributeName = attribute.name
            val type = attribute.type
            val poetTypeName = attribute.getPoetTypeName()
            val attributeNameAsProperty = attributeName.toPropertyName()
            val hasFixedValue = attribute.fixed is OptionalNullableString.Present
            val hasDefaultValue = attribute.default is OptionalNullableString.Present

            if (type is Type.Enum) {
                val enumClassName = attributeName.toClassName()
                val enumBuilder = TypeSpec.enumBuilder(enumClassName)
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter("enumValue", String::class)
                            .build()
                    ).addProperty(
                        PropertySpec.builder("enumValue", String::class)
                            .initializer("enumValue")
                            .build()
                    )
                type.values.forEach { enumValue ->
                    val enumConstantName = enumValue.name.toEnumConstantName()
                    enumBuilder.addEnumConstant(
                        enumConstantName, TypeSpec.anonymousClassBuilder()
                            .addSuperclassConstructorParameter("%S", enumValue.value)
                            .build()
                    )
                }

                classBuilder.addType(enumBuilder.build())
            }
            if (!hasFixedValue) {
                val parameterBuilder = ParameterSpec.builder(attributeNameAsProperty, poetTypeName)
                if (hasDefaultValue) {
                    val defaultValue = attribute.default.requireValue()
                    if (defaultValue == null) {
                        if (type.isNullable) {
                            parameterBuilder.defaultValue("null")
                        } else {
                            throw IllegalArgumentException("Cannot set default null value to a non-nullable type '$type' for attribute '$attributeName'")
                        }
                    } else {
                        parameterBuilder.defaultValue(
                            type.formatValueForCodeInsertion(attribute, defaultValue)
                        )
                    }
                }
                constructorBuilder.addParameter(parameterBuilder.build())
                classBuilder.addProperty(
                    PropertySpec.builder(attributeNameAsProperty, poetTypeName)
                        .initializer(attributeNameAsProperty)
                        .mutable(attribute.mutable)
                        .build()
                )
                attributesPropertyValues.add("\"$attributeName\" to $attributeNameAsProperty")
            } else {
                val fixedValue = attribute.fixed.requireValue()
                if (fixedValue == null) {
                    if (type.isNullable) {
                        attributesPropertyValues.add("\"$attributeName\" to null")
                    } else {
                        throw IllegalArgumentException("Cannot set fixed null value to a non-nullable type '$type' for attribute '$attributeName'")
                    }
                } else {
                    attributesPropertyValues.add(
                        "\"$attributeName\" to ${
                            type.formatValueForCodeInsertion(attribute, fixedValue)
                        }"
                    )
                }
            }
        }
        classBuilder.primaryConstructor(constructorBuilder.build())
        classBuilder.addProperty(
            PropertySpec.builder(
                "attributes",
                Map::class.asClassName().parameterizedBy(
                    String::class.asClassName(),
                    ANY.copy(nullable = true)
                ),
                KModifier.OVERRIDE
            ).initializer(codeBlock = buildCodeBlock {
                addStatement("mapOf<String, Any?>(")

                attributesPropertyValues.forEach { statement ->
                    addStatement("  $statement,")
                }
                add(")")
            }).build()
        )
        classBuilder.addProperty(
            PropertySpec.builder(
                "eventValue",
                String::class,
                KModifier.OVERRIDE
            ).initializer("%S", event.value).build()
        )

        FileSpec.builder(params.packageName, eventName)
            .addType(classBuilder.build())
            .build()
    }

    files.forEach { file -> file.writeTo(System.out) }
    return files
}

private fun Attribute.getPoetTypeName(): TypeName = when (this.type) {
    Type.Primitive.STRING -> STRING
    Type.Primitive.NULLABLE_STRING -> STRING.copy(nullable = true)
    Type.Primitive.INTEGER -> INT
    Type.Primitive.NULLABLE_INTEGER -> INT.copy(nullable = true)
    Type.Primitive.DOUBLE -> DOUBLE
    Type.Primitive.NULLABLE_DOUBLE -> DOUBLE.copy(nullable = true)
    Type.Primitive.BOOLEAN -> BOOLEAN
    Type.Primitive.NULLABLE_BOOLEAN -> BOOLEAN.copy(nullable = true)
    Type.Primitive.LONG -> LONG
    Type.Primitive.NULLABLE_LONG -> LONG.copy(nullable = true)
    is Type.Enum -> ClassName("", this.name.toClassName())
}
