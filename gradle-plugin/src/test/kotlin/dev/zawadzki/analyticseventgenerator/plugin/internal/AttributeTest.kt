package dev.zawadzki.analyticseventgenerator.plugin.internal

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.SerializationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class AttributeTest {

    private lateinit var input: String

    private lateinit var attribute: Attribute

    @TestFactory
    fun `deserialize primitive attribute`() = listOf(
        Type.Primitive.STRING,
        Type.Primitive.NULLABLE_STRING,
        Type.Primitive.BOOLEAN,
        Type.Primitive.NULLABLE_BOOLEAN,
        Type.Primitive.INTEGER,
        Type.Primitive.NULLABLE_INTEGER,
        Type.Primitive.LONG,
        Type.Primitive.NULLABLE_LONG,
        Type.Primitive.DOUBLE,
        Type.Primitive.NULLABLE_DOUBLE
    ).map { type ->
        DynamicTest.dynamicTest("deserialize primitive attribute of type ${type.value}") {
            input = """
                type: ${type.value}
            """.trimIndent()

            deserialize()

            attribute.validate()
            val expectedAttribute = attribute(type = type)
            assertEquals(expectedAttribute, attribute)
        }
    }

    @TestFactory
    fun `deserialize primitive attribute with non-null default value`() = listOf(
        Type.Primitive.STRING to "xx",
        Type.Primitive.NULLABLE_STRING to "xx",
        Type.Primitive.BOOLEAN to "true",
        Type.Primitive.NULLABLE_BOOLEAN to "true",
        Type.Primitive.INTEGER to "123",
        Type.Primitive.NULLABLE_INTEGER to "123",
        Type.Primitive.LONG to "123",
        Type.Primitive.NULLABLE_LONG to "123",
        Type.Primitive.DOUBLE to "123.0",
        Type.Primitive.NULLABLE_DOUBLE to "123.0"
    ).map { (type, default) ->
        DynamicTest.dynamicTest("deserialize primitive attribute with non-null default value of type ${type.value}") {
            input = """
                type: ${type.value}
                default: $default
            """.trimIndent()

            deserialize()

            attribute.validate()
            val expectedAttribute =
                attribute(type = type, default = OptionalNullableString.Present(default))
            assertEquals(expectedAttribute, attribute)
        }
    }

    @TestFactory
    fun `deserialize primitive attribute with null default value`() = listOf(
        Type.Primitive.STRING to true,
        Type.Primitive.NULLABLE_STRING to false,
        Type.Primitive.BOOLEAN to true,
        Type.Primitive.NULLABLE_BOOLEAN to false,
        Type.Primitive.INTEGER to true,
        Type.Primitive.NULLABLE_INTEGER to false,
        Type.Primitive.LONG to true,
        Type.Primitive.NULLABLE_LONG to false,
        Type.Primitive.DOUBLE to true,
        Type.Primitive.NULLABLE_DOUBLE to false
    ).map { (type, throwsError) ->
        DynamicTest.dynamicTest("deserialize primitive attribute with null default value ${type.value}") {
            input = """
                type: ${type.value}
                default: null
            """.trimIndent()

            deserialize()

            val expectedAttribute =
                attribute(type = type, default = OptionalNullableString.Present(null))
            assertEquals(expectedAttribute, attribute)
            if (throwsError) {
                assertThrows<SerializationException> {
                    attribute.validate()
                }
            } else {
                attribute.validate()
            }
        }
    }

    @TestFactory
    fun `deserialize primitive attribute with non-null fixed value`() = listOf(
        Type.Primitive.STRING to "xx",
        Type.Primitive.NULLABLE_STRING to "xx",
        Type.Primitive.BOOLEAN to "true",
        Type.Primitive.NULLABLE_BOOLEAN to "true",
        Type.Primitive.INTEGER to "123",
        Type.Primitive.NULLABLE_INTEGER to "123",
        Type.Primitive.LONG to "123",
        Type.Primitive.NULLABLE_LONG to "123",
        Type.Primitive.DOUBLE to "123.0",
        Type.Primitive.NULLABLE_DOUBLE to "123.0"
    ).map { (type, fixed) ->
        DynamicTest.dynamicTest("deserialize primitive attribute of type ${type.value}") {
            input = """
                type: ${type.value}
                fixed: $fixed
            """.trimIndent()

            deserialize()

            attribute.validate()
            val expectedAttribute =
                attribute(type = type, fixed = OptionalNullableString.Present(fixed))
            assertEquals(expectedAttribute, attribute)
        }
    }

    @TestFactory
    fun `deserialize primitive attribute with null fixed value`() = listOf(
        Type.Primitive.STRING to true,
        Type.Primitive.NULLABLE_STRING to false,
        Type.Primitive.BOOLEAN to true,
        Type.Primitive.NULLABLE_BOOLEAN to false,
        Type.Primitive.INTEGER to true,
        Type.Primitive.NULLABLE_INTEGER to false,
        Type.Primitive.LONG to true,
        Type.Primitive.NULLABLE_LONG to false,
        Type.Primitive.DOUBLE to true,
        Type.Primitive.NULLABLE_DOUBLE to false
    ).map { (type, throwsError) ->
        DynamicTest.dynamicTest("deserialize primitive attribute with null default value ${type.value}") {
            input = """
                type: ${type.value}
                fixed: null
            """.trimIndent()

            deserialize()

            val expectedAttribute =
                attribute(type = type, fixed = OptionalNullableString.Present(null))
            assertEquals(expectedAttribute, attribute)
            if (throwsError) {
                assertThrows<SerializationException> {
                    attribute.validate()
                }
            } else {
                attribute.validate()
            }
        }
    }

    @Test
    fun `deserialize attribute with description`() {
        input = """
            type: String
            description: super important arg
            """.trimIndent()

        deserialize()

        attribute.validate()
        val expectedAttribute =
            attribute(type = Type.Primitive.STRING, description = "super important arg")
        assertEquals(expectedAttribute, attribute)
    }

    @Test
    fun `deserialize mutable attribute`() {
        input = """
            type: String
            mutable: true
            """.trimIndent()

        deserialize()

        attribute.validate()
        val expectedAttribute = attribute(type = Type.Primitive.STRING, mutable = true)
        assertEquals(expectedAttribute, attribute)
    }

    @Test
    fun `deserialize explicit immutable attribute`() {
        input = """
            type: String
            mutable: false
            """.trimIndent()

        deserialize()

        attribute.validate()
        val expectedAttribute = attribute(type = Type.Primitive.STRING, mutable = false)
        assertEquals(expectedAttribute, attribute)
    }

    @Test
    fun `deserialize enum with defined types`() {
        input = """
            type:
              enum:
                default:
                  value: "DEFAULT val"
                  description: "default type"
                custom:
                  value: "CUSTOM val"
            """.trimIndent()

        deserialize()

        attribute.validate()
        val expectedAttribute = attribute(
            type = Type.Enum(
                listOf(
                    Type.Enum.EnumValue(
                        name = "default", value = "DEFAULT val", description = "default type"
                    ), Type.Enum.EnumValue(
                        name = "custom", value = "CUSTOM val", description = ""
                    )
                )
            )
        )
        assertEquals(expectedAttribute, attribute)
    }

    @Test
    fun `deserialize enum with default value`() {
        input = """
            type:
              enum:
                default:
                  value: "DEFAULT val"
                  description: "default type"
                custom:
                  value: "CUSTOM val"
            default: default
            """.trimIndent()

        deserialize()

        attribute.validate()
        val expectedAttribute = attribute(
            type = Type.Enum(
                listOf(
                    Type.Enum.EnumValue(
                        name = "default", value = "DEFAULT val", description = "default type"
                    ), Type.Enum.EnumValue(
                        name = "custom", value = "CUSTOM val", description = ""
                    )
                )
            ), default = OptionalNullableString.Present("default")
        )
        assertEquals(expectedAttribute, attribute)
    }

    @Test
    fun `deserialize enum with default null-value not supported`() {
        input = """
            type:
              enum:
                default:
                  value: "DEFAULT val"
                  description: "default type"
                custom:
                  value: "CUSTOM val"
            default: null
            """.trimIndent()

        deserialize()

        assertThrows<SerializationException> {
            attribute.validate()
        }
    }

    @Test
    fun `deserialize enum with fixed value`() {
        input = """
            type:
              enum:
                default:
                  value: "DEFAULT val"
                  description: "default type"
                custom:
                  value: "CUSTOM val"
            fixed: custom
            """.trimIndent()

        deserialize()

        attribute.validate()
        val expectedAttribute = attribute(
            type = Type.Enum(
                listOf(
                    Type.Enum.EnumValue(
                        name = "default", value = "DEFAULT val", description = "default type"
                    ), Type.Enum.EnumValue(
                        name = "custom", value = "CUSTOM val", description = ""
                    )
                )
            ), fixed = OptionalNullableString.Present("custom")
        )
        assertEquals(expectedAttribute, attribute)
    }

    @Test
    fun `deserialize enum with fixed null-value not supported`() {
        input = """
            type:
              enum:
                default:
                  value: "DEFAULT val"
                  description: "default type"
                custom:
                  value: "CUSTOM val"
            fixed: null
            """.trimIndent()

        deserialize()

        assertThrows<SerializationException> {
            attribute.validate()
        }
    }

    private fun deserialize() {
        attribute = Yaml.default.decodeFromString(Attribute.serializer(), input)
    }

    private fun attribute(
        type: Type,
        mutable: Boolean = false,
        description: String = "",
        default: OptionalNullableString = OptionalNullableString.Missing,
        fixed: OptionalNullableString = OptionalNullableString.Missing
    ) = Attribute(
        name = "",
        type = type,
        mutable = mutable,
        description = description,
        default = default,
        fixed = fixed
    )
}
