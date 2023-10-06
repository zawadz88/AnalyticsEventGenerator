package dev.zawadzki.analyticseventgenerator.plugin

import com.charleskorn.kaml.Yaml
import org.junit.Test

class DeserializationTest {

    @Test
    fun `Parse sample YAML`() {
        val input = readLine("/sample.yaml")

        val document = Yaml.default.decodeFromString(Document.serializer(), input)

        println(document)
    }
}
