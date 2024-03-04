package dev.zawadzki.analyticseventgenerator.plugin.internal

import com.charleskorn.kaml.Yaml
import com.squareup.kotlinpoet.FileSpec
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class CodeGenerationTest {

    private lateinit var document: Document

    private lateinit var fileSpecs: List<FileSpec>

    @Test
    fun `generate events with attributes that are different cases of enum type`() {
        @Language("yaml")
        document = """
            events:
              event with enums:
                value: event_1
                attributes:
                  regularEnum:
                    type: 
                      enum:  
                        unknown:
                          value: "Unknown"
                        hello friend:
                          value: "Hi \"friend\"!"
                          description: "foo"
                        value_with_underscore:
                          value: some value
                  enum_with_default:
                    type: 
                      enum:  
                        foo:
                          value: "Foo"
                        BAR:
                          value: "Bar"
                        foo bar:
                          value: Foo Bar
                        BAR_FOO:
                          value: BAR FOO
                    default: foo bar
                  enum with fixed:
                    type: 
                      enum:  
                        foo:
                          value: "Foo"
                        Bar:
                          value: "Bar"
                        fooBar:
                          value: Foo Bar
                    fixed: fooBar
        """.toDocument()

        generateCode()

        assertEquals(1, fileSpecs.size)
        val fileSpec = fileSpecs.first()
        assertEquals("SampleEventWithEnums", fileSpec.name)
        val fileContent = fileSpec.toString()
        assertEquals(
            """
            package com.example

            import dev.zawadzki.analyticseventgenerator.runtime.AbstractEvent
            import kotlin.Any
            import kotlin.OptIn
            import kotlin.String
            import kotlin.collections.Map
            import kotlin.js.ExperimentalJsExport
            import kotlin.js.JsExport
            import kotlin.js.JsName

            @JsExport
            @OptIn(ExperimentalJsExport::class)
            public data class SampleEventWithEnums(
              public val regularEnum: RegularEnum,
              public val enumWithDefault: EnumWithDefault = EnumWithDefault.FOO_BAR,
            ) : AbstractEvent() {
              @JsExport.Ignore
              override val attributes: Map<String, Any?>
                get() {
                  return mapOf<String, Any?>(
                    "regularEnum" to regularEnum.enumValue,
                    "enum_with_default" to enumWithDefault.enumValue,
                    "enum with fixed" to EnumWithFixed.FOOBAR.enumValue,
                  )
                }

              override val eventValue: String = "event_1"

              @JsName("init1")
              public constructor(regularEnum: RegularEnum) : this(regularEnum, EnumWithDefault.FOO_BAR)

              public enum class RegularEnum(
                public val enumValue: String,
              ) {
                UNKNOWN("Unknown"),
                /**
                 * foo
                 */
                HELLO_FRIEND("Hi \"friend\"!"),
                VALUE_WITH_UNDERSCORE("some value"),
                ;
              }

              public enum class EnumWithDefault(
                public val enumValue: String,
              ) {
                FOO("Foo"),
                BAR("Bar"),
                FOO_BAR("Foo Bar"),
                BAR_FOO("BAR FOO"),
                ;
              }

              public enum class EnumWithFixed(
                public val enumValue: String,
              ) {
                FOO("Foo"),
                BAR("Bar"),
                FOOBAR("Foo Bar"),
                ;
              }
            }
            
        """.trimIndent(), fileContent
        )
    }

    @Test
    fun `generate events with no default or fixed attributes`() {
        @Language("yaml")
        document = """
            events:
              Event1:
                value: event_1
                description: Sample description
                attributes:
                  stringAttr:
                    type: String
                    description: sample string attribute
                  stringAttrNullable:
                    type: String?
                  intAttr:
                    type: Integer
                  intAttrNullable:
                    type: Integer?
                  longAttr:
                    type: Long
                  longAttrNullable:
                    type: Long?
                    description: sample long nullable attribute
              Event2:
                value: event_2
                attributes:
                  doubleAttr:
                    type: Double
                  doubleAttrNullable:
                    type: Double?
                  booleanAttr:
                    type: Boolean
                  booleanAttrNullable:
                    type: Boolean?
        """.toDocument()

        generateCode()

        assertEquals(2, fileSpecs.size)
        val firstFileSpec = fileSpecs.first()
        assertEquals("SampleEvent1", firstFileSpec.name)
        val firstFileContent = firstFileSpec.toString()
        assertEquals(
            """
            package com.example

            import dev.zawadzki.analyticseventgenerator.runtime.AbstractEvent
            import kotlin.Any
            import kotlin.Int
            import kotlin.Long
            import kotlin.OptIn
            import kotlin.String
            import kotlin.Suppress
            import kotlin.collections.Map
            import kotlin.js.ExperimentalJsExport
            import kotlin.js.JsExport
            
            /**
             * Sample description
             */
            @JsExport
            @OptIn(ExperimentalJsExport::class)
            public data class SampleEvent1(
              /**
               * sample string attribute
               */
              public val stringAttr: String,
              public val stringAttrNullable: String?,
              public val intAttr: Int,
              public val intAttrNullable: Int?,
              @Suppress("NON_EXPORTABLE_TYPE")
              public val longAttr: Long,
              /**
               * sample long nullable attribute
               */
              @Suppress("NON_EXPORTABLE_TYPE")
              public val longAttrNullable: Long?,
            ) : AbstractEvent() {
              @JsExport.Ignore
              override val attributes: Map<String, Any?>
                get() {
                  return mapOf<String, Any?>(
                    "stringAttr" to stringAttr,
                    "stringAttrNullable" to stringAttrNullable,
                    "intAttr" to intAttr,
                    "intAttrNullable" to intAttrNullable,
                    "longAttr" to longAttr,
                    "longAttrNullable" to longAttrNullable,
                  )
                }
            
              override val eventValue: String = "event_1"
            }
            
        """.trimIndent(), firstFileContent
        )
        val secondFileSpec = fileSpecs[1]
        assertEquals("SampleEvent2", secondFileSpec.name)
        val secondFileContent = secondFileSpec.toString()
        assertEquals(
            """
            package com.example

            import dev.zawadzki.analyticseventgenerator.runtime.AbstractEvent
            import kotlin.Any
            import kotlin.Boolean
            import kotlin.Double
            import kotlin.OptIn
            import kotlin.String
            import kotlin.collections.Map
            import kotlin.js.ExperimentalJsExport
            import kotlin.js.JsExport

            @JsExport
            @OptIn(ExperimentalJsExport::class)
            public data class SampleEvent2(
              public val doubleAttr: Double,
              public val doubleAttrNullable: Double?,
              public val booleanAttr: Boolean,
              public val booleanAttrNullable: Boolean?,
            ) : AbstractEvent() {
              @JsExport.Ignore
              override val attributes: Map<String, Any?>
                get() {
                  return mapOf<String, Any?>(
                    "doubleAttr" to doubleAttr,
                    "doubleAttrNullable" to doubleAttrNullable,
                    "booleanAttr" to booleanAttr,
                    "booleanAttrNullable" to booleanAttrNullable,
                  )
                }

              override val eventValue: String = "event_2"
            }
            
        """.trimIndent(), secondFileContent
        )
    }

    @Test
    fun `generate events with default attributes`() {
        TODO("generate.yaml, one event with some defaults, one with only defaults")
    }

    @Test
    fun `generate events with mutable attributes`() {
        TODO("generate.yaml, one event with some mutables, one with only mutables")
    }

    @Test
    fun `generate events with fixed attributes`() {
        TODO("generate.yaml, one event with some fixed, one with only fixed")
    }

    @Test
    fun `cannot generate event with non-null attribute having default null value`() {
        TODO("")
    }

    @Test
    fun `cannot generate event with non-null attribute having fixed null value`() {
        TODO("")
    }

    private fun generateCode() {
        fileSpecs = generateCode(
            CodeGenerationParams(
                "Sample",
                "com.example"
            ), document
        )
    }

    private fun String.toDocument() =
        Yaml.default.decodeFromString(Document.serializer(), this.trimIndent())
}

