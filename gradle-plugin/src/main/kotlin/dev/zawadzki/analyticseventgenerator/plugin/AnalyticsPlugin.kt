package dev.zawadzki.analyticseventgenerator.plugin

import dev.zawadzki.analyticseventgenerator.plugin.internal.CodeGenerationParams
import dev.zawadzki.analyticseventgenerator.plugin.internal.generateCode
import dev.zawadzki.analyticseventgenerator.plugin.internal.readDocuments
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import java.io.File

class AnalyticsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create<AnalyticsExtension>("analyticsEvents")
        project.tasks.register<GenerateAnalyticsEventsTask>("generateAnalyticsEvents") {
            inputFiles.from(extension.inputFiles)
            outputDirectory.set(extension.outputDirectory)
            prefix.set(extension.prefix)
            packageName.set(extension.packageName)
        }
    }
}

abstract class GenerateAnalyticsEventsTask : DefaultTask() {

    @get:InputFiles
    abstract val inputFiles: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Input
    abstract val packageName: Property<String>

    @get:Input
    abstract val prefix: Property<String>

    private val outputDirectoryFile: File
        get() = outputDirectory.get().asFile

    private val codeGenerationParams: CodeGenerationParams
        get() = CodeGenerationParams(
            prefix = prefix.get(),
            packageName = packageName.get()
        )

    @TaskAction
    fun execute() {
        logger.info(
            """
            Generating analytics events with params:
            packageName: ${packageName.orNull}
            prefix: ${prefix.orNull}
            inputFiles: ${inputFiles.files}
            outputDirectory: ${outputDirectory.orNull?.asFile}
            """.trimIndent()
        )

        val documents = readDocuments(inputFiles).getOrThrow()

        logger.info("Docs: $documents")

        val generatedFileSpecs =
            documents.flatMap { doc -> generateCode(codeGenerationParams, doc) }

        logger.info("generatedFileSpecs: $generatedFileSpecs")

        generatedFileSpecs.forEach { it.writeTo(outputDirectoryFile) }
    }
}
