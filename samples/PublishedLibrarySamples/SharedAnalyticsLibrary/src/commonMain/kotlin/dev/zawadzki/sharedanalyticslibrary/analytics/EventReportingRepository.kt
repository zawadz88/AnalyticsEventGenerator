package dev.zawadzki.sharedanalyticslibrary.analytics

import dev.zawadzki.analyticseventgenerator.runtime.AbstractEvent
import kotlin.js.JsExport

@JsExport
interface EventReportingRepository {

    fun reportEvent(event: AbstractEvent)
}
