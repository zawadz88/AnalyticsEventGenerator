package dev.zawadzki.sharedanalyticslibrary.di

import dev.zawadzki.sharedanalyticslibrary.analytics.EventReportingRepository
import dev.zawadzki.sharedanalyticslibrary.analytics.EventReportingRepositoryImpl
import dev.zawadzki.sharedanalyticslibrary.getPlatform
import dev.zawadzki.sharedanalyticslibrary.platform.IoDispatcher
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlin.js.JsExport

@JsExport
val eventReportingRepository: EventReportingRepository by lazy {
    EventReportingRepositoryImpl(httpClient, CoroutineScope(IoDispatcher))
}

/**
 * Just a Mock API that will accept anything.
 * @see <a href="https://mockapi.io">mockapi.io</a>
 */
private const val BASE_API_URL = "https://65d86ac5c96fbb24c1bb7996.mockapi.io/"

private val httpClient: HttpClient by lazy {
    HttpClient {
        install(ContentNegotiation) {
            json()
        }
        defaultRequest {
            url(BASE_API_URL)
            contentType(ContentType.Application.Json)
        }
        install(UserAgent) {
            agent = "SharedAnalyticsLibrary client/${getPlatform().name}"
        }
    }
}
