package dev.zawadzki.sharedanalyticslibrary

import dev.zawadzki.analyticseventgenerator.runtime.AbstractEvent
import dev.zawadzki.sharedanalyticslibrary.event.SampleActionWithTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.reflect.KMutableProperty0

fun executeAction() {
    val eventWithTimer = SampleActionWithTimer(duration = 0)
    CoroutineScope(Dispatchers.Main).launch {
        eventWithTimer.startTimer(eventWithTimer::duration)
        delay(2_500L)
        eventWithTimer.stopTimer(eventWithTimer::duration)
        println("Sending event: $eventWithTimer")
    }
}


private fun AbstractEvent.startTimer(mutableProperty: KMutableProperty0<Long>) {
    mutableProperty.set(Clock.System.now().toEpochMilliseconds())
}

private fun AbstractEvent.stopTimer(mutableProperty: KMutableProperty0<Long>) {
    mutableProperty.set(Clock.System.now().toEpochMilliseconds() - mutableProperty.get())
}
