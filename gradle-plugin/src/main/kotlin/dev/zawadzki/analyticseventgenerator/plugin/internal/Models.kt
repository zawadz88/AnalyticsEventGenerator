package dev.zawadzki.analyticseventgenerator.plugin.internal

import com.charleskorn.kaml.YamlInput
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlScalar
import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
internal data class Document(
    @Serializable(with = EventListDeserializer::class) val events: List<Event>
)

@Serializable
internal data class Event(
    @Transient val name: String = "",
    val value: String,
    val description: String = "",
    @Serializable(with = AttributeListDeserializer::class) val attributes: List<Attribute>
)

@Serializable
internal data class Attribute(
    @Transient val name: String = "",
    val type: Type,
    val mutable: Boolean = false,
    val description: String = "",
    val default: OptionalNullableString = OptionalNullableString.Missing,
    val fixed: OptionalNullableString = OptionalNullableString.Missing
) {

    @Throws(SerializationException::class)
    fun validate() {
        if (type.isNullable) return

        if (default is OptionalNullableString.Present && default.value == null) {
            throw SerializationException("Non-null type '${type.displayableName}' for attribute '$name' had a default value set to null")
        }

        if (fixed is OptionalNullableString.Present && fixed.value == null) {
            throw SerializationException("Non-null type '${type.displayableName}' for attribute '$name' had a fixed value set to null")
        }
        // TODO: validate enum is not an empty list
    }
}

@Serializable(with = TypeDeserializer::class)
internal sealed interface Type {

    val displayableName: String

    val isNullable: Boolean
        get() = false

    fun formatValueForCodeInsertion(attribute: Attribute, valueToInsert: String): String

    fun formatAttributeNameForAttributesMap(attribute: Attribute): String =
        attribute.name.toPropertyName()

    fun formatValueForAttributesMap(attribute: Attribute, valueToInsert: String): String =
        formatValueForCodeInsertion(attribute, valueToInsert)

    // TODO: REMOVE DUPLICATES for formatValueForCodeInsertion
    enum class Primitive(val value: String) : Type {
        STRING("String") {
            override fun formatValueForCodeInsertion(
                attribute: Attribute,
                valueToInsert: String
            ): String =
                "\"${valueToInsert.replace("\"", "\\\"")}\""
        },
        NULLABLE_STRING("String?") {
            override val isNullable: Boolean = true

            override fun formatValueForCodeInsertion(
                attribute: Attribute,
                valueToInsert: String
            ): String =
                "\"${valueToInsert.replace("\"", "\\\"")}\""
        },
        BOOLEAN("Boolean") {
            override fun formatValueForCodeInsertion(
                attribute: Attribute,
                valueToInsert: String
            ): String =
                valueToInsert.lowercase().toBooleanStrict().toString()
        },
        NULLABLE_BOOLEAN("Boolean?") {
            override val isNullable: Boolean = true

            override fun formatValueForCodeInsertion(
                attribute: Attribute,
                valueToInsert: String
            ): String =
                valueToInsert.lowercase().toBooleanStrict().toString()
        },
        INTEGER("Integer") {
            override fun formatValueForCodeInsertion(
                attribute: Attribute,
                valueToInsert: String
            ): String =
                valueToInsert.toInt().toString()
        },
        NULLABLE_INTEGER("Integer?") {
            override val isNullable: Boolean = true

            override fun formatValueForCodeInsertion(
                attribute: Attribute,
                valueToInsert: String
            ): String =
                valueToInsert.toInt().toString()
        },
        LONG("Long") {
            override fun formatValueForCodeInsertion(
                attribute: Attribute,
                valueToInsert: String
            ): String =
                valueToInsert.toLong().toString()
        },
        NULLABLE_LONG("Long?") {
            override val isNullable: Boolean = true

            override fun formatValueForCodeInsertion(
                attribute: Attribute,
                valueToInsert: String
            ): String =
                valueToInsert.toLong().toString()
        },
        DOUBLE("Double") {
            override fun formatValueForCodeInsertion(
                attribute: Attribute,
                valueToInsert: String
            ): String =
                valueToInsert.toDouble().toString()
        },
        NULLABLE_DOUBLE("Double?") {
            override val isNullable: Boolean = true

            override fun formatValueForCodeInsertion(
                attribute: Attribute,
                valueToInsert: String
            ): String =
                valueToInsert.toDouble().toString()
        };

        override val displayableName by ::value
    }

    @Serializable
    data class Enum(
        @SerialName("enum")
        @Serializable(with = EnumValueListDeserializer::class)
        val values: List<EnumValue>
    ) : Type {

        override val displayableName
            get() = "enum"

        override fun formatValueForCodeInsertion(
            attribute: Attribute,
            valueToInsert: String
        ): String = "${attribute.name.toClassName()}.${valueToInsert.toEnumConstantName()}"

        override fun formatValueForAttributesMap(
            attribute: Attribute,
            valueToInsert: String
        ): String = "${formatValueForCodeInsertion(attribute, valueToInsert)}.enumValue"
        override fun formatAttributeNameForAttributesMap(attribute: Attribute): String =
            "${super.formatAttributeNameForAttributesMap(attribute)}.enumValue"

        @Serializable
        data class EnumValue(
            @Transient val name: String = "",
            val value: String,
            val description: String = ""
        )
    }
}

/**
 * Used to distinguish between when a property is missing vs when it's set to a null value.
 */
@Serializable(with = OptionalNullableStringDeserializer::class)
internal sealed class OptionalNullableString {

    abstract fun requireValue(): String?

    @Serializable
    object Missing : OptionalNullableString() {
        override fun requireValue(): String =
            throw IllegalStateException("Cannot require a missing value")
    }

    @Serializable
    data class Present(val value: String?) : OptionalNullableString() {
        override fun requireValue(): String? = value
    }
}

internal object OptionalNullableStringDeserializer : KSerializer<OptionalNullableString> {

    private val nullableStringSerializer = String.serializer().nullable

    override val descriptor: SerialDescriptor = nullableStringSerializer.descriptor

    override fun deserialize(decoder: Decoder): OptionalNullableString =
        OptionalNullableString.Present(nullableStringSerializer.deserialize(decoder))

    override fun serialize(encoder: Encoder, value: OptionalNullableString) =
        throw NotImplementedError("Serialization not supported!")
}

@OptIn(ExperimentalSerializationApi::class)
internal object EventListDeserializer : KSerializer<List<Event>> {

    override val descriptor: SerialDescriptor = ContextualSerializer(List::class).descriptor

    override fun deserialize(decoder: Decoder): List<Event> {
        val yamlInput = decoder.beginStructure(descriptor) as YamlInput

        val map = decoder.decodeSerializableValue(
            MapSerializer(String.serializer(), Event.serializer())
        )

        return map.map { (key, value) -> value.copy(name = key) }.also {
            yamlInput.endStructure(descriptor)
        }
    }

    override fun serialize(encoder: Encoder, value: List<Event>) =
        throw NotImplementedError("Serialization not supported!")
}

@OptIn(ExperimentalSerializationApi::class)
internal object AttributeListDeserializer : KSerializer<List<Attribute>> {

    override val descriptor: SerialDescriptor = ContextualSerializer(List::class).descriptor

    override fun deserialize(decoder: Decoder): List<Attribute> {
        val yamlInput = decoder.beginStructure(descriptor) as YamlInput

        val map = decoder.decodeSerializableValue(
            MapSerializer(String.serializer(), Attribute.serializer())
        )

        return map.map { (key, value) ->
            value.copy(name = key).also(Attribute::validate)
        }.also {
            yamlInput.endStructure(descriptor)
        }
    }

    override fun serialize(encoder: Encoder, value: List<Attribute>) =
        throw NotImplementedError("Serialization not supported!")
}

@OptIn(ExperimentalSerializationApi::class)
internal object EnumValueListDeserializer : KSerializer<List<Type.Enum.EnumValue>> {

    override val descriptor: SerialDescriptor = ContextualSerializer(List::class).descriptor

    override fun deserialize(decoder: Decoder): List<Type.Enum.EnumValue> {
        val yamlInput = decoder.beginStructure(descriptor) as YamlInput

        val map = decoder.decodeSerializableValue(
            MapSerializer(String.serializer(), Type.Enum.EnumValue.serializer())
        )

        return map.map { (key, value) -> value.copy(name = key) }.also {
            yamlInput.endStructure(descriptor)
        }
    }

    override fun serialize(encoder: Encoder, value: List<Type.Enum.EnumValue>) =
        throw NotImplementedError("Serialization not supported!")
}

@OptIn(ExperimentalSerializationApi::class)
internal object TypeDeserializer : KSerializer<Type> {
    private val enumSerializer = Type.Enum.serializer()

    override val descriptor: SerialDescriptor = ContextualSerializer(Type::class).descriptor

    override fun serialize(encoder: Encoder, value: Type) =
        throw NotImplementedError("Serialization not supported!")

    override fun deserialize(decoder: Decoder): Type {
        val yamlInput = decoder.beginStructure(descriptor) as YamlInput

        return when (val node = yamlInput.node) {
            is YamlScalar -> Type.Primitive.values().firstOrNull { it.value == node.content }
                ?: throw SerializationException("No Primitive type found for '${node.content}'")

            is YamlMap -> yamlInput.decodeSerializableValue(enumSerializer)
            else -> throw SerializationException("Value is neither a map nor a string.")
        }
    }
}
