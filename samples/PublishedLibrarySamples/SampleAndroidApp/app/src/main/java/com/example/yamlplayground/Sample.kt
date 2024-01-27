package com.example.yamlplayground

import dev.zawadzki.sharedanalyticslibrary.event.SampleAdditionalButtonTapped
import dev.zawadzki.sharedanalyticslibrary.event.SampleButtonTapped
import dev.zawadzki.sharedanalyticslibrary.event.SampleSomething


class Sample {

    fun main() {
        SampleSomething(
            isEnabled = true,
            clickCount = 1,
            duration = 2000L,
            accuracy = 0.5,
            myType = SampleSomething.MyType.CUSTOM
        )
        SampleButtonTapped(
            buttonId = "buttonId1", someOptional = null
        )
        SampleAdditionalButtonTapped(
            buttonId = "buttonId2"
        )
    }
}
