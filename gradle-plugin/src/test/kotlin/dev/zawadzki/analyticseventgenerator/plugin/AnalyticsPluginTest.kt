package dev.zawadzki.analyticseventgenerator.plugin

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

internal class AnalyticsPluginTest {

    companion object {
        private const val GENERATE_ANALYTICS_EVENTS_TASK_NAME = ":generateAnalyticsEvents"
    }

    @TempDir
    lateinit var testProjectDir: File

    private lateinit var buildFile: File

    private lateinit var yamlFile: File

    private lateinit var result: BuildResult

    @BeforeEach
    fun setup() {
        buildFile = File(testProjectDir, "build.gradle")
        buildFile.writeText(
            """
            plugins {
                id 'dev.zawadzki.analyticseventgenerator'
            }
            
        """.trimIndent()
        )
        yamlFile = File(testProjectDir, "sample.yaml")
    }

    @Test
    fun `generate kotlin classes with default output location`() {
        givenDefaultExtensionInBuildFile()
        writeValidEventsToYamlFile()

        runTask()

        assertEquals(TaskOutcome.SUCCESS, result.task(GENERATE_ANALYTICS_EVENTS_TASK_NAME)?.outcome)
        val generatedFile = File(testProjectDir, "build/generated/source/events/dev/zawadzki/sample/event/SampleButtonTapped.kt")
        assertTrue(generatedFile.exists())
        assertTrue(generatedFile.isFile)
    }

    @Test
    fun `generate kotlin classes with custom output location`() {
        buildFile.appendText(
            """
            analyticsEvents {
                prefix = "Sample"
                packageName = "dev.zawadzki.sample.event"
                inputFiles.setFrom(layout.projectDirectory.file("sample.yaml"))
                outputDirectory = layout.buildDirectory.dir("another")
            }
        """.trimIndent()
        )
        writeValidEventsToYamlFile()

        runTask()

        assertEquals(TaskOutcome.SUCCESS, result.task(GENERATE_ANALYTICS_EVENTS_TASK_NAME)?.outcome)
        val generatedFile = File(testProjectDir, "build/another/dev/zawadzki/sample/event/SampleButtonTapped.kt")
        assertTrue(generatedFile.exists())
        assertTrue(generatedFile.isFile)
    }

    @Test
    fun `do not generate kotlin classes when input contains invalid schema`() {
        givenDefaultExtensionInBuildFile()
        yamlFile.writeText(
            """
                events:
            """.trimIndent()
        )

        runTask(expectFailure = true)

        assertEquals(TaskOutcome.FAILED, result.task(GENERATE_ANALYTICS_EVENTS_TASK_NAME)?.outcome)
        assertTrue(
            result.output.contains("AnalyticsGenerationException: Source: file .*sample.yaml, cause: Value for 'events' is invalid: Unexpected null or empty value for non-null field".toRegex())
        ) { "Was: ${result.output}" }
    }

    @Test
    fun `do not generate kotlin classes when input contains invalid content`() {
        givenDefaultExtensionInBuildFile()
        val yamlInputWithNullDefaultValueForNonNullAttribute = """
                events:
                  ButtonTapped:
                    value: "button_tapped"
                    attributes:
                      buttonId:
                        type: String
                        default: null
            """.trimIndent()
        yamlFile.writeText(yamlInputWithNullDefaultValueForNonNullAttribute)

        runTask(expectFailure = true)

        assertEquals(TaskOutcome.FAILED, result.task(GENERATE_ANALYTICS_EVENTS_TASK_NAME)?.outcome)
        assertTrue(
            result.output.contains("AnalyticsGenerationException: Source: file .*sample.yaml, cause: Non-null type 'String' for attribute 'buttonId' had a default value set to null".toRegex())
        ) { "Was: ${result.output}" }
    }

    private fun givenDefaultExtensionInBuildFile() {
        buildFile.appendText(
            """
                analyticsEvents {
                    prefix = "Sample"
                    packageName = "dev.zawadzki.sample.event"
                    inputFiles.setFrom(layout.projectDirectory.file("sample.yaml"))
                }
            """.trimIndent()
        )
    }

    private fun runTask(expectFailure: Boolean = false) {
        result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("generateAnalyticsEvents")
            .withPluginClasspath()
            .run {
                if (expectFailure) {
                    buildAndFail()
                } else {
                    build()
                }
            }
    }

    private fun writeValidEventsToYamlFile() {
        yamlFile.writeText(
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
