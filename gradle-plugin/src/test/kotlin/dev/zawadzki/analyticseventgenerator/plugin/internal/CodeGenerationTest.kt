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
        @Language("yaml")
        document = """
            events:
              Event1:
                value: event_1
                attributes:
                  stringAttr:
                    type: String
                    default: sample string
                  stringAttrNullable:
                    type: String?
                    default: sample nullable string
                  intAttr:
                    type: Integer
                    default: 10
                  intAttrNullable:
                    type: Integer?
                    default: null
                  longAttr:
                    type: Long
                    default: 10000000000000000
                  longAttrNullable:
                    type: Long?
                    default: 10000000000000000
                  doubleAttr:
                    type: Double
                    default: 10.25
                  doubleAttrNullable:
                    type: Double?
                    default: 10.25
                  booleanAttr:
                    type: Boolean
                    default: true
                  booleanAttrNullable:
                    type: Boolean?
                    default: false
        """.toDocument()

        generateCode()

        assertEquals(1, fileSpecs.size)
        val fileSpec = fileSpecs.first()
        assertEquals("SampleEvent1", fileSpec.name)
        val fileContent = fileSpec.toString()
        assertEquals(
            """
            package com.example

            import dev.zawadzki.analyticseventgenerator.runtime.AbstractEvent
            import kotlin.Any
            import kotlin.Boolean
            import kotlin.Double
            import kotlin.Int
            import kotlin.Long
            import kotlin.OptIn
            import kotlin.String
            import kotlin.Suppress
            import kotlin.collections.Map
            import kotlin.js.ExperimentalJsExport
            import kotlin.js.JsExport
            import kotlin.js.JsName

            @JsExport
            @OptIn(ExperimentalJsExport::class)
            public data class SampleEvent1(
              public val stringAttr: String = "sample string",
              public val stringAttrNullable: String? = "sample nullable string",
              public val intAttr: Int = 10,
              public val intAttrNullable: Int? = null,
              @Suppress("NON_EXPORTABLE_TYPE")
              public val longAttr: Long = 10000000000000000,
              @Suppress("NON_EXPORTABLE_TYPE")
              public val longAttrNullable: Long? = 10000000000000000,
              public val doubleAttr: Double = 10.25,
              public val doubleAttrNullable: Double? = 10.25,
              public val booleanAttr: Boolean = true,
              public val booleanAttrNullable: Boolean? = false,
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
                    "doubleAttr" to doubleAttr,
                    "doubleAttrNullable" to doubleAttrNullable,
                    "booleanAttr" to booleanAttr,
                    "booleanAttrNullable" to booleanAttrNullable,
                  )
                }

              override val eventValue: String = "event_1"

              @JsName("init9")
              public constructor(
                stringAttr: String,
                stringAttrNullable: String?,
                intAttr: Int,
                intAttrNullable: Int?,
                @Suppress("NON_EXPORTABLE_TYPE") longAttr: Long,
                @Suppress("NON_EXPORTABLE_TYPE") longAttrNullable: Long?,
                doubleAttr: Double,
                doubleAttrNullable: Double?,
                booleanAttr: Boolean,
              ) : this(stringAttr, stringAttrNullable, intAttr, intAttrNullable, longAttr, longAttrNullable,
                  doubleAttr, doubleAttrNullable, booleanAttr, false)

              @JsName("init8")
              public constructor(
                stringAttr: String,
                stringAttrNullable: String?,
                intAttr: Int,
                intAttrNullable: Int?,
                @Suppress("NON_EXPORTABLE_TYPE") longAttr: Long,
                @Suppress("NON_EXPORTABLE_TYPE") longAttrNullable: Long?,
                doubleAttr: Double,
                doubleAttrNullable: Double?,
              ) : this(stringAttr, stringAttrNullable, intAttr, intAttrNullable, longAttr, longAttrNullable,
                  doubleAttr, doubleAttrNullable, true, false)

              @JsName("init7")
              public constructor(
                stringAttr: String,
                stringAttrNullable: String?,
                intAttr: Int,
                intAttrNullable: Int?,
                @Suppress("NON_EXPORTABLE_TYPE") longAttr: Long,
                @Suppress("NON_EXPORTABLE_TYPE") longAttrNullable: Long?,
                doubleAttr: Double,
              ) : this(stringAttr, stringAttrNullable, intAttr, intAttrNullable, longAttr, longAttrNullable,
                  doubleAttr, 10.25, true, false)

              @JsName("init6")
              public constructor(
                stringAttr: String,
                stringAttrNullable: String?,
                intAttr: Int,
                intAttrNullable: Int?,
                @Suppress("NON_EXPORTABLE_TYPE") longAttr: Long,
                @Suppress("NON_EXPORTABLE_TYPE") longAttrNullable: Long?,
              ) : this(stringAttr, stringAttrNullable, intAttr, intAttrNullable, longAttr, longAttrNullable,
                  10.25, 10.25, true, false)

              @JsName("init5")
              public constructor(
                stringAttr: String,
                stringAttrNullable: String?,
                intAttr: Int,
                intAttrNullable: Int?,
                @Suppress("NON_EXPORTABLE_TYPE") longAttr: Long,
              ) : this(stringAttr, stringAttrNullable, intAttr, intAttrNullable, longAttr, 10000000000000000,
                  10.25, 10.25, true, false)

              @JsName("init4")
              public constructor(
                stringAttr: String,
                stringAttrNullable: String?,
                intAttr: Int,
                intAttrNullable: Int?,
              ) : this(stringAttr, stringAttrNullable, intAttr, intAttrNullable, 10000000000000000,
                  10000000000000000, 10.25, 10.25, true, false)

              @JsName("init3")
              public constructor(
                stringAttr: String,
                stringAttrNullable: String?,
                intAttr: Int,
              ) : this(stringAttr, stringAttrNullable, intAttr, null, 10000000000000000, 10000000000000000,
                  10.25, 10.25, true, false)

              @JsName("init2")
              public constructor(stringAttr: String, stringAttrNullable: String?) : this(stringAttr,
                  stringAttrNullable, 10, null, 10000000000000000, 10000000000000000, 10.25, 10.25, true, false)

              @JsName("init1")
              public constructor(stringAttr: String) : this(stringAttr, "sample nullable string", 10, null,
                  10000000000000000, 10000000000000000, 10.25, 10.25, true, false)

              @JsName("init0")
              public constructor() : this("sample string", "sample nullable string", 10, null,
                  10000000000000000, 10000000000000000, 10.25, 10.25, true, false)
            }
            
        """.trimIndent(), fileContent
        )
    }

    @Test
    fun `generate events with mutable attributes`() {
        @Language("yaml")
        document = """
            events:
              Event1:
                value: event_1
                attributes:
                  stringAttr:
                    type: String
                    mutable: true
                  stringAttrNullable:
                    type: String?
                    mutable: True
                  intAttr:
                    type: Integer
                    mutable: false
                  intAttrNullable:
                    type: Integer?
                    mutable: False
                  longAttr:
                    type: Long
                    mutable: true
                  longAttrNullable:
                    type: Long?
                    mutable: true
                  doubleAttr:
                    type: Double
                    mutable: true
                  doubleAttrNullable:
                    type: Double?
                    mutable: true
                  booleanAttr:
                    type: Boolean
                    mutable: true
                  booleanAttrNullable:
                    type: Boolean?
                    mutable: true
        """.toDocument()

        generateCode()

        assertEquals(1, fileSpecs.size)
        val fileSpec = fileSpecs.first()
        assertEquals("SampleEvent1", fileSpec.name)
        val fileContent = fileSpec.toString()
        assertEquals(
            """
            package com.example

            import dev.zawadzki.analyticseventgenerator.runtime.AbstractEvent
            import kotlin.Any
            import kotlin.Boolean
            import kotlin.Double
            import kotlin.Int
            import kotlin.Long
            import kotlin.OptIn
            import kotlin.String
            import kotlin.Suppress
            import kotlin.collections.Map
            import kotlin.js.ExperimentalJsExport
            import kotlin.js.JsExport

            @JsExport
            @OptIn(ExperimentalJsExport::class)
            public data class SampleEvent1(
              public var stringAttr: String,
              public var stringAttrNullable: String?,
              public val intAttr: Int,
              public val intAttrNullable: Int?,
              @Suppress("NON_EXPORTABLE_TYPE")
              public var longAttr: Long,
              @Suppress("NON_EXPORTABLE_TYPE")
              public var longAttrNullable: Long?,
              public var doubleAttr: Double,
              public var doubleAttrNullable: Double?,
              public var booleanAttr: Boolean,
              public var booleanAttrNullable: Boolean?,
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
                    "doubleAttr" to doubleAttr,
                    "doubleAttrNullable" to doubleAttrNullable,
                    "booleanAttr" to booleanAttr,
                    "booleanAttrNullable" to booleanAttrNullable,
                  )
                }

              override val eventValue: String = "event_1"
            }
            
        """.trimIndent(), fileContent
        )
    }

    @Test
    fun `generate events with fixed attributes`() {
        @Language("yaml")
        document = """
            events:
              Event1:
                value: event_1
                attributes:
                  stringAttr:
                    type: String
                    fixed: sample string
                  stringAttrNullable:
                    type: String?
                    fixed: sample nullable string
                  intAttr:
                    type: Integer
                    fixed: 10
                  intAttrNullable:
                    type: Integer?
                    fixed: null
                  longAttr:
                    type: Long
                    fixed: 10000000000000000
                  longAttrNullable:
                    type: Long?
                    fixed: 10000000000000000
                  doubleAttr:
                    type: Double
                    fixed: 10.25
                  doubleAttrNullable:
                    type: Double?
                    fixed: 10.25
                  booleanAttr:
                    type: Boolean
                    fixed: True
                  booleanAttrNullable:
                    type: Boolean?
                    fixed: false
        """.toDocument()

        generateCode()

        assertEquals(1, fileSpecs.size)
        val fileSpec = fileSpecs.first()
        assertEquals("SampleEvent1", fileSpec.name)
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

            @JsExport
            @OptIn(ExperimentalJsExport::class)
            public data class SampleEvent1() : AbstractEvent() {
              @JsExport.Ignore
              override val attributes: Map<String, Any?>
                get() {
                  return mapOf<String, Any?>(
                    "stringAttr" to "sample string",
                    "stringAttrNullable" to "sample nullable string",
                    "intAttr" to 10,
                    "intAttrNullable" to null,
                    "longAttr" to 10000000000000000,
                    "longAttrNullable" to 10000000000000000,
                    "doubleAttr" to 10.25,
                    "doubleAttrNullable" to 10.25,
                    "booleanAttr" to true,
                    "booleanAttrNullable" to false,
                  )
                }

              override val eventValue: String = "event_1"
            }
            
        """.trimIndent(), fileContent
        )
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

