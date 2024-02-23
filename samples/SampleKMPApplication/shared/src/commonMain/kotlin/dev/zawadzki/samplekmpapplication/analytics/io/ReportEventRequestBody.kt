package dev.zawadzki.samplekmpapplication.analytics.io

import kotlinx.serialization.Serializable

@Serializable
internal data class ReportEventRequestBody(
    val events: List<Event>
) {

    @Serializable
    internal data class Event(
        val name: String,
        val attributes: Map<String, String>,
        val metrics: Map<String, Double>,
    )
}
