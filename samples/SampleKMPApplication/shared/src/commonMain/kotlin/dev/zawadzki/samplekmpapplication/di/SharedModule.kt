package dev.zawadzki.samplekmpapplication.di

import dev.zawadzki.samplekmpapplication.analytics.SharedAnalyticsModule
import dev.zawadzki.samplekmpapplication.getPlatform
import dev.zawadzki.samplekmpapplication.platform.IoDispatcher
import io.ktor.client.HttpClient
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

internal const val IO = "IO"

/**
 * Just a Mock API that will accept anything.
 * @see <a href="https://mockapi.io">mockapi.io</a>
 */
private const val BASE_API_URL = "https://65d86ac5c96fbb24c1bb7996.mockapi.io/"

@Module(includes = [SharedAnalyticsModule::class])
@ComponentScan
class SharedModule {

    @Factory
    @Named(IO)
    fun provideCoroutineScope(
        @Named(IO) ioDispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(ioDispatcher)

    @Single
    @Named(IO)
    fun provideIoDispatcher(): CoroutineDispatcher = IoDispatcher

    @Single
    fun provideHttpClient(): HttpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
        defaultRequest {
            url(BASE_API_URL)
            contentType(ContentType.Application.Json)
        }
        install(UserAgent) {
            agent = "KMP event sample app/${getPlatform().name}"
        }
    }
}
