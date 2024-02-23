package dev.zawadzki.samplekmpapplication.analytics

import dev.zawadzki.analyticseventgenerator.runtime.AbstractEvent
import dev.zawadzki.samplekmpapplication.analytics.extension.filterReportableAttributes
import dev.zawadzki.samplekmpapplication.analytics.extension.filterReportableMetrics
import dev.zawadzki.samplekmpapplication.analytics.io.ReportEventRequestBody
import dev.zawadzki.samplekmpapplication.core.suspendRunCatching
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class EventReportingRepositoryImpl(
    private val httpClient: HttpClient,
    private val coroutineScope: CoroutineScope
) : EventReportingRepository {

    override fun reportEvent(event: AbstractEvent) {
        /*
        Note: in a real app, you'd prefer to send these event in batches & periodically
        when there's internet connectivity. Something similar to what Google Analytics on mobile does.
        Configuration might be different for a JS Browser client though.
        Also, do some proper error handling.
        */
        coroutineScope.launch {
            val reportedEvent = ReportEventRequestBody.Event(
                name = event.eventValue,
                attributes = event.filterReportableAttributes(),
                metrics = event.filterReportableMetrics()
            )
            suspendRunCatching {
                val response: HttpResponse = httpClient.post("events") {
                    setBody(
                        ReportEventRequestBody(
                            events = listOf(reportedEvent)
                        )
                    )
                }
                println("response: $response: ${response.bodyAsText()}")
            }.onFailure {
                println("failure: $it")
            }
        }
    }

}
