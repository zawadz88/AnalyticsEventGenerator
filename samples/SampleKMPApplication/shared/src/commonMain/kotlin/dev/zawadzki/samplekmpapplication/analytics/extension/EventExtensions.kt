package dev.zawadzki.samplekmpapplication.analytics.extension

import dev.zawadzki.analyticseventgenerator.runtime.AbstractEvent

internal fun AbstractEvent.filterReportableAttributes(): Map<String, String> =
    buildMap {
        for ((k: String, v: Any?) in attributes) {
            if (v == null) continue

            if (v is Number) continue

            put(k, v.toString())
        }
    }

internal fun AbstractEvent.filterReportableMetrics(): Map<String, Double> =
    buildMap {
        for ((k: String, v: Any?) in attributes) {
            if (v == null) continue

            if (v is Number) {
                put(k, v.toDouble())
            }
        }
    }