package dev.zawadzki.analyticseventgenerator.plugin

import com.charleskorn.kaml.Yaml
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import java.io.File

internal fun Task.readDocuments(inputFiles: FileCollection): List<Document> =
    inputFiles.filter { file: File? ->
        val isValidFileExtension = file?.extension == "yaml"
        if (!isValidFileExtension) {
            logger.debug("Excluding file '$file' as it does not have .yaml extension")
        }
        isValidFileExtension
    }.map { file: File -> Yaml.default.decodeFromString(Document.serializer(), file.readText()) }

//internal fun Task.readDocuments(inputFiles: FileCollection): List<Document> =
//    inputFiles.flatMap {
//        it.listFiles { file -> file.extension == "yaml" }?.asList().orEmpty()
//    }
//        .map { file: File -> Yaml.default.decodeFromString(Document.serializer(), file.readText()) }
