package dev.zawadzki.analyticseventgenerator.plugin.internal

internal class AnalyticsGenerationException(source: String, cause: Throwable) : Exception(cause) {

    override val message: String = "Source: $source, cause: ${cause.message}"
}
