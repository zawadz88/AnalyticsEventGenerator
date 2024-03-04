package dev.zawadzki.analyticseventgenerator.plugin.internal

import com.charleskorn.kaml.Yaml
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class EventTest {

    private lateinit var input: String

    private lateinit var event: Event

    @Test
    fun `deserialize event with single attribute`() {
        input = """    
            value: "button_tapped"
            description: I'm a button tap event
            attributes:
              attr1:
                type: String
            """.trimIndent()

        deserialize()

        val expectedEvent = event(
            value = "button_tapped",
            description = "I'm a button tap event",
            attributes = listOf(
                Attribute(
                    name = "attr1",
                    type = Type.Primitive.STRING
                )
            )
        )
        assertEquals(expectedEvent, event)
    }

    @Test
    fun `deserialize event with multiple attributes`() {
        input = """    
            value: "button_tapped"
            description: I'm a button tap event
            attributes:
              attr1:
                type: String
              attr2:
                type: Boolean?
            """.trimIndent()

        deserialize()

        val expectedEvent = event(
            value = "button_tapped",
            description = "I'm a button tap event",
            attributes = listOf(
                Attribute(
                    name = "attr1",
                    type = Type.Primitive.STRING
                ),
                Attribute(
                    name = "attr2",
                    type = Type.Primitive.NULLABLE_BOOLEAN
                ),
            )
        )
        assertEquals(expectedEvent, event)
    }

    @Test
    fun `deserialize event with no attributes`() {
        input = """    
            value: "button_tapped"
            description: I'm a button tap event
            """.trimIndent()

        deserialize()

        val expectedEvent = event(value = "button_tapped", description = "I'm a button tap event")
        assertEquals(expectedEvent, event)
    }

    @Test
    fun `deserialize event without description`() {
        input = """    
            value: "button_tapped"
            """.trimIndent()

        deserialize()

        val expectedEvent = event(value = "button_tapped", description = "")
        assertEquals(expectedEvent, event)

    }

    private fun event(
        value: String,
        description: String,
        attributes: List<Attribute> = emptyList()
    ) = Event(
        name = "",
        value = value,
        description = description,
        attributes = attributes
    )

    private fun deserialize() {
        event = Yaml.default.decodeFromString(Event.serializer(), input)
    }
}
