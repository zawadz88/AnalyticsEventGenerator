package dev.zawadzki.analyticseventgenerator.plugin.internal

import com.charleskorn.kaml.EmptyYamlDocumentException
import com.charleskorn.kaml.Yaml
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DocumentTest {

    private lateinit var input: String

    private lateinit var document: Document

    @Test
    fun `deserialize doc with single event`() {
        input = """
            events:
              ButtonTapped:
                value: button_tapped
                attributes:
                  buttonId:
                    type: String
        """.trimIndent()

        deserialize()

        val expectedDocument = Document(
            listOf(
                Event(
                    name = "ButtonTapped",
                    value = "button_tapped",
                    description = "",
                    attributes = listOf(
                        Attribute(
                            "buttonId",
                            Type.Primitive.STRING
                        )
                    )
                )
            )
        )
        assertEquals(expectedDocument, document)
    }

    @Test
    fun `deserialize doc with multiple events`() {
        input = """
            events:
              ButtonTapped:
                value: button_tapped
                attributes:
                  buttonId:
                    type: String?
              WorkflowCompleted:
                value: workflow_completed
                attributes:
                  id:
                    type: String
        """.trimIndent()

        deserialize()

        val expectedDocument = Document(
            listOf(
                Event(
                    name = "ButtonTapped",
                    value = "button_tapped",
                    description = "",
                    attributes = listOf(
                        Attribute(
                            "buttonId",
                            Type.Primitive.NULLABLE_STRING
                        )
                    )
                ),
                Event(
                    name = "WorkflowCompleted",
                    value = "workflow_completed",
                    description = "",
                    attributes = listOf(
                        Attribute(
                            "id",
                            Type.Primitive.STRING
                        )
                    )
                )
            )
        )
        assertEquals(expectedDocument, document)
    }

    @Test
    fun `cannot deserialize empty doc`() {
        input = ""

        assertThrows<EmptyYamlDocumentException> {
            deserialize()
        }
    }

    private fun deserialize() {
        document = Yaml.default.decodeFromString(Document.serializer(), input)
    }
}
