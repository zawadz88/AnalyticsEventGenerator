package dev.zawadzki.samplekmpapplication.di

import dev.zawadzki.samplekmpapplication.analytics.EventReportingRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module
import kotlin.js.JsExport

@JsExport
object KoinHelper : KoinComponent {

    val eventReportingRepository: EventReportingRepository
        get() = get()

    fun initKoin() {
        startKoin {
            modules(SharedModule().module)
        }
    }
}
