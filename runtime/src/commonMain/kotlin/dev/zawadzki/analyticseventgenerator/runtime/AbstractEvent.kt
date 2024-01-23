package dev.zawadzki.analyticseventgenerator.runtime

import kotlin.js.JsExport

@JsExport
abstract class AbstractEvent {

    abstract val eventValue: String

    // Map Kotlin type not supported in JavaScript:
    // https://kotlinlang.org/docs/js-to-kotlin-interop.html#kotlin-types-in-javascript
    @JsExport.Ignore
    abstract val attributes: Map<String, Any?>
}
