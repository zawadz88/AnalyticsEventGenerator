package dev.zawadzki.analyticseventgenerator.plugin.internal

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.buildCodeBlock
import dev.zawadzki.analyticseventgenerator.runtime.AbstractEvent

private const val ENUM_VALUE_PROPERTY_NAME = "enumValue"

private val jsExportClassName by lazy { ClassName("kotlin.js", "JsExport") }

private val jsNameClassName by lazy { ClassName("kotlin.js", "JsName") }

private val jsExportIgnoreClassName by lazy { ClassName("kotlin.js", "JsExport", "Ignore") }

private val optInClassName by lazy { ClassName("kotlin", "OptIn") }

private val experimentalJsExportClassName by lazy { ClassName("kotlin.js", "ExperimentalJsExport") }

internal fun generateCode(params: CodeGenerationParams, document: Document): List<FileSpec> {
    val files = document.events.map { event ->
        val eventName = params.prefix + event.name.toClassName()
        val classBuilder = TypeSpec.classBuilder(eventName)
            .superclass(AbstractEvent::class)
            .addKdoc(event.description)
            .addModifiers(KModifier.DATA)
        val constructorBuilder = FunSpec.constructorBuilder()
        val attributesPropertyValues = mutableListOf<String>()

        parseAttributes(
            event = event,
            classBuilder = classBuilder,
            constructorBuilder = constructorBuilder,
            attributesPropertyValues = attributesPropertyValues
        )
        classBuilder.primaryConstructor(constructorBuilder.build())
        addSecondaryConstructors(
            constructorBuilder = constructorBuilder,
            classBuilder = classBuilder
        )

        addAttributesProperty(
            classBuilder = classBuilder,
            attributesPropertyValues = attributesPropertyValues
        )
        addEventValueProperty(classBuilder = classBuilder, event = event)
        addClassAnnotations(classBuilder)

        createFileSpec(params = params, eventName = eventName, classBuilder = classBuilder)
    }
    return files
}

private fun parseAttributes(
    event: Event,
    classBuilder: TypeSpec.Builder,
    constructorBuilder: FunSpec.Builder,
    attributesPropertyValues: MutableList<String>
) {
    for (attribute in event.attributes) {
        val type = attribute.type
        val hasFixedValue = attribute.fixed is OptionalNullableString.Present

        if (type is Type.Enum) {
            addNewEnumType(
                attributeName = attribute.name,
                type = type,
                classBuilder = classBuilder
            )
        }
        if (!hasFixedValue) {
            addStandardAttribute(
                attribute = attribute,
                constructorBuilder = constructorBuilder,
                classBuilder = classBuilder,
                attributesPropertyValues = attributesPropertyValues
            )
        } else {
            addAttributeWithFixedValue(
                attribute = attribute,
                attributesPropertyValues = attributesPropertyValues
            )
        }
    }
}

private fun addStandardAttribute(
    attribute: Attribute,
    constructorBuilder: FunSpec.Builder,
    classBuilder: TypeSpec.Builder,
    attributesPropertyValues: MutableList<String>
) {
    val type = attribute.type
    val attributeName = attribute.name
    val hasDefaultValue = attribute.default is OptionalNullableString.Present
    val poetTypeName = attribute.getPoetTypeName()
    val attributeNameAsProperty = attributeName.toPropertyName()
    val parameterBuilder = ParameterSpec.builder(attributeNameAsProperty, poetTypeName)
    if (hasDefaultValue) {
        val defaultValue = attribute.default.requireValue()
        if (defaultValue == null) {
            parameterBuilder.defaultValue("null")
        } else {
            parameterBuilder.defaultValue(
                type.formatValueForCodeInsertion(attribute, defaultValue)
            )
        }
    }
    if (attribute.type == Type.Primitive.LONG || attribute.type == Type.Primitive.NULLABLE_LONG) {
        parameterBuilder.addAnnotation(
            AnnotationSpec.builder(Suppress::class)
                .addMember("%S", "NON_EXPORTABLE_TYPE")
                .build()
        )
    }
    constructorBuilder.addParameter(parameterBuilder.build())
    classBuilder.addProperty(
        PropertySpec.builder(attributeNameAsProperty, poetTypeName)
            .initializer(attributeNameAsProperty)
            .mutable(attribute.mutable)
            .apply {
                attribute.description.takeIf { it.isNotBlank() }?.let { addKdoc(it) }
             }
            .build()
    )
    attributesPropertyValues.add(
        "\"$attributeName\" to ${
            type.formatAttributeNameForAttributesMap(
                attribute
            )
        }"
    )
}

private fun addAttributeWithFixedValue(
    attribute: Attribute,
    attributesPropertyValues: MutableList<String>,
) {
    val attributeName = attribute.name
    val type = attribute.type
    val fixedValue = attribute.fixed.requireValue()
    if (fixedValue == null) {
        if (type.isNullable) {
            attributesPropertyValues.add("\"$attributeName\" to null")
        }
    } else {
        attributesPropertyValues.add(
            "\"$attributeName\" to ${
                type.formatValueForAttributesMap(attribute, fixedValue)
            }"
        )
    }
}


private fun addNewEnumType(
    attributeName: String,
    type: Type.Enum,
    classBuilder: TypeSpec.Builder
) {
    val enumClassName = attributeName.toClassName()
    val enumBuilder = TypeSpec.enumBuilder(enumClassName)
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter(ENUM_VALUE_PROPERTY_NAME, String::class)
                .build()
        ).addProperty(
            PropertySpec.builder(ENUM_VALUE_PROPERTY_NAME, String::class)
                .initializer(ENUM_VALUE_PROPERTY_NAME)
                .build()
        )
    type.values.forEach { enumValue ->
        val enumConstantName = enumValue.name.toEnumConstantName()
        enumBuilder.addEnumConstant(
            enumConstantName, TypeSpec.anonymousClassBuilder()
                .addSuperclassConstructorParameter("%S", enumValue.value)
                .addKdoc(enumValue.description)
                .build()
        )
    }

    classBuilder.addType(enumBuilder.build())
}

private fun createFileSpec(
    params: CodeGenerationParams,
    eventName: String,
    classBuilder: TypeSpec.Builder
) = FileSpec.builder(params.packageName, eventName)
    .addType(classBuilder.build())
    .build()

private fun addClassAnnotations(classBuilder: TypeSpec.Builder) {
    classBuilder.addAnnotation(jsExportClassName)
    classBuilder.addAnnotation(
        AnnotationSpec.builder(optInClassName)
            .addMember("%T::class", experimentalJsExportClassName)
            .build()
    )
}

private fun addEventValueProperty(
    classBuilder: TypeSpec.Builder,
    event: Event
) {
    classBuilder.addProperty(
        PropertySpec.builder(
            "eventValue",
            String::class,
            KModifier.OVERRIDE
        ).initializer("%S", event.value).build()
    )
}

private fun addAttributesProperty(
    classBuilder: TypeSpec.Builder,
    attributesPropertyValues: MutableList<String>
) {
    classBuilder.addProperty(
        PropertySpec.builder(
            "attributes",
            Map::class.asClassName().parameterizedBy(
                String::class.asClassName(),
                ANY.copy(nullable = true)
            ),
            KModifier.OVERRIDE
        ).getter(
            FunSpec.getterBuilder()
                .addCode(buildCodeBlock {
                    add("return")
                    addStatement(" mapOf<String, Any?>(")
                    attributesPropertyValues.forEach { statement ->
                        addStatement("  $statement,")
                    }
                    add(")")
                }).build()
        )
            .addAnnotation(jsExportIgnoreClassName)
            .build()
    )
}

/**
 * Adds secondary constructors if needed.
 * This is so that we have constructors generated for default attributes,
 * which are not supported on JS and Darwin.
 * The constructors are similar to the ones generated for JVM via @JvmOverloads.
 */
private fun addSecondaryConstructors(
    constructorBuilder: FunSpec.Builder,
    classBuilder: TypeSpec.Builder
) {
    val primaryConstructorParameters = constructorBuilder.parameters
    val allParamsSize = primaryConstructorParameters.size - 1
    for (outerLoopIndex in allParamsSize downTo 0) {
        val topCheckedParamSpec = primaryConstructorParameters[outerLoopIndex]
        if (topCheckedParamSpec.defaultValue != null) {
            val secondaryConstructor = FunSpec.constructorBuilder()
            val thisConstructorCodeBlocks = mutableListOf<CodeBlock>()
            // secondary constructors need a different name in JS:
            // https://kotlinlang.org/docs/js-to-kotlin-interop.html#jsname-annotation
            secondaryConstructor.addAnnotation(
                AnnotationSpec.builder(jsNameClassName)
                    .addMember("%S", "init$outerLoopIndex")
                    .build()
            )
            for (innerLoopIndex in 0..allParamsSize) {
                val secondaryParamSpec = primaryConstructorParameters[innerLoopIndex]
                val defaultValueCodeBlock = secondaryParamSpec.defaultValue
                if (defaultValueCodeBlock != null) {
                    if (innerLoopIndex < outerLoopIndex) {
                        val secondaryParamSpecBuilder = secondaryParamSpec.toBuilder()
                        secondaryParamSpecBuilder.defaultValue(null)
                        secondaryConstructor.addParameter(secondaryParamSpecBuilder.build())
                        val paramBlock =
                            CodeBlock.builder().add(secondaryParamSpec.name).build()
                        thisConstructorCodeBlocks.add(paramBlock)
                    } else {
                        thisConstructorCodeBlocks.add(defaultValueCodeBlock)
                    }
                } else {
                    secondaryConstructor.addParameter(secondaryParamSpec)
                    val paramBlock = CodeBlock.builder().add(secondaryParamSpec.name).build()
                    thisConstructorCodeBlocks.add(paramBlock)
                }
            }
            secondaryConstructor.callThisConstructor(thisConstructorCodeBlocks)
            classBuilder.addFunction(secondaryConstructor.build())
        }
    }
}
