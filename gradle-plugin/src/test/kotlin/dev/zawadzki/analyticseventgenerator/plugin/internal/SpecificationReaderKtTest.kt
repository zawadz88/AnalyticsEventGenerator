package dev.zawadzki.analyticseventgenerator.plugin.internal

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

internal class SpecificationReaderKtTest {

    @TempDir
    lateinit var testProjectDir: File

    private val project by lazy {
        ProjectBuilder.builder().build()
    }

    private val task by lazy {
        project.task("helloWorld")
    }

    @Test
    fun `returns empty list when file collection is empty`() {
        val emptyFileCollection = project.objects.fileCollection()

        val result = task.readDocuments(emptyFileCollection)

        assertTrue(result.isSuccess)
        assertEquals(emptyList<Document>(), result.getOrNull())
    }

    @Test
    fun `returns empty list when file collection is an empty directory`() {
        val fileCollection = project.objects.fileCollection().apply {
            setFrom(testProjectDir.listFiles())
        }

        val result = task.readDocuments(fileCollection)

        assertTrue(result.isSuccess)
        assertEquals(emptyList<Document>(), result.getOrNull())
    }

    @Test
    fun `returns empty list when file collection contains only invalid files`() {
        File(testProjectDir, "sample.xml").createNewFile()
        val fileCollection = project.objects.fileCollection().apply {
            setFrom(testProjectDir.listFiles())
        }

        val result = task.readDocuments(fileCollection)

        assertTrue(result.isSuccess)
        assertEquals(emptyList<Document>(), result.getOrNull())
    }

    @Test
    fun `returns filtered list when file collection contains valid & invalid files`() {
        File(testProjectDir, "sample.yaml").writeValidEvents()
        val fileCollection = project.objects.fileCollection().apply {
            setFrom(testProjectDir.listFiles())
        }

        val result = task.readDocuments(fileCollection)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().size)
    }

    @Test
    fun `returns failure when one of the files is invalid`() {
        File(testProjectDir, "sample.yaml").createNewFile()
        val fileCollection = project.objects.fileCollection().apply {
            setFrom(testProjectDir.listFiles())
        }

        val result = task.readDocuments(fileCollection)

        assertTrue(result.isFailure)
        val exceptionMessage = result.exceptionOrNull()?.message.orEmpty()
        assertTrue(exceptionMessage.contains("Source: file .*sample.yaml, cause: The YAML document is empty".toRegex())) {
            "Actual message - \"$exceptionMessage\""
        }
    }

    private fun File.writeValidEvents() {
        this.writeText(
            """
                events:
                  ButtonTapped:
                    value: "button_tapped"
                    description: I'm a button tap event
                    attributes:
                      buttonId:
                        type: String
                        description: "unique identifier of the button"
            """.trimIndent()
        )
    }
}
