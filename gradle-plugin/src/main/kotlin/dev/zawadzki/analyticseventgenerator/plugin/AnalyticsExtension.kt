package dev.zawadzki.analyticseventgenerator.plugin

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property

open class AnalyticsExtension(project: Project) {
    val inputFiles: ConfigurableFileCollection = project.objects.fileCollection()

    val outputDirectory: DirectoryProperty = project.objects.directoryProperty()

    val packageName: Property<String> = project.objects.property()

    val prefix: Property<String> = project.objects.property()

    init {
        // defaults
        prefix.set("")
        packageName.set("")
        outputDirectory.set(
            project.layout.buildDirectory.asFile.get()
                .resolve(DEFAULT_DIRECTORY_GENERATED)
                .resolve(DEFAULT_DIRECTORY_SOURCE)
                .resolve(DEFAULT_DIRECTORY_EVENTS)
        )
    }
}

private const val DEFAULT_DIRECTORY_GENERATED = "generated"
private const val DEFAULT_DIRECTORY_SOURCE = "source"
private const val DEFAULT_DIRECTORY_EVENTS = "events"
