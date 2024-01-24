package dev.zawadzki.sharedanalyticslibrary

import dev.zawadzki.sharedanalyticslibrary.event.SampleAdditionalButtonTapped
import dev.zawadzki.sharedanalyticslibrary.event.SampleButtonTapped
import dev.zawadzki.sharedanalyticslibrary.event.SampleSomething

class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
        val sampleSomethingEvent = SampleSomething(
            isEnabled = true,
            clickCount = 1,
            duration = 2000L,
            accuracy = 0.5,
            myType = SampleSomething.MyType.CUSTOM
        )
        val sampleButtonTapped = SampleButtonTapped(
            buttonId = "buttonId1", someOptional = null
        )
        val sampleAdditionalButtonTapped = SampleAdditionalButtonTapped(
            buttonId = "buttonId2"
        )

        return """Hello, ${platform.name}!
            | $sampleSomethingEvent
            | $sampleButtonTapped
            | $sampleAdditionalButtonTapped
        """.trimMargin()
    }
}
