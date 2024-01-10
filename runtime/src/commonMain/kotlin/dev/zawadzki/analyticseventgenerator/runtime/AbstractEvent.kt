package dev.zawadzki.analyticseventgenerator.runtime

abstract class AbstractEvent {

    abstract val eventValue: String

    abstract val attributes: Map<String, Any?>
}
