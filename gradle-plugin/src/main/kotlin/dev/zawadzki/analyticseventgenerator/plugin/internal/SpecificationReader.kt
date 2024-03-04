package dev.zawadzki.analyticseventgenerator.plugin.internal

import com.charleskorn.kaml.Yaml
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import java.io.File

internal fun Task.readDocuments(inputFiles: FileCollection): Result<List<Document>> {
    val documents: List<Document> = inputFiles.filter { file: File? ->
        val isValidFileExtension = file?.extension == "yaml"
        if (!isValidFileExtension) {
            logger.warn("Excluding file '$file' as it does not have .yaml extension")
        }
        isValidFileExtension
    }.map { file: File ->
        try {
            Yaml.default.decodeFromString(Document.serializer(), file.readText())
        } catch (ex: Exception) {
            return Result.failure(AnalyticsGenerationException("file ${file.path}", ex))
        }
    }
    return Result.success(documents)
}
