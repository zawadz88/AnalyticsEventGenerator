package dev.zawadzki.analyticseventgenerator.plugin.internal

import com.charleskorn.kaml.Yaml
import org.junit.Test

class CodeGenerationTest {

    @Test
    fun generateFromSimpleSample() {
        val input = readLine("/simple_sample.yaml")
        val document = Yaml.default.decodeFromString(Document.serializer(), input)

        generateCode(document)
    }

    @Test
    fun generateFromSample() {
        val input = readLine("/sample.yaml")
        val document = Yaml.default.decodeFromString(Document.serializer(), input)

        generateCode(document)
    }

    private fun generateCode(document: Document) {
        generateCode(
            CodeGenerationParams(
                "Sample",
                "com.example"
            ), document
        )
    }
}
