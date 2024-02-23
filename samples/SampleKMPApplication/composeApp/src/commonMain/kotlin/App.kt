import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.zawadzki.samplekmpapplication.di.eventReportingRepository
import dev.zawadzki.samplekmpapplication.event.SampleActionWithTimer
import dev.zawadzki.samplekmpapplication.event.SampleSomething

@Composable
fun App() {
    MaterialTheme {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                val sampleEvent = SampleSomething(
                    isEnabled = true,
                    clickCount = 1,
                    duration = 2000L,
                    accuracy = 0.5,
                    myType = SampleSomething.MyType.CUSTOM
                )
                eventReportingRepository.reportEvent(sampleEvent)
            }) {
                Text("Send sample event")
            }
            Button(onClick = {
                val eventWithTimer = SampleActionWithTimer(duration = 0)
                eventWithTimer.duration = 2500L
                eventReportingRepository.reportEvent(eventWithTimer)
            }) {
                Text("Send event with duration")
            }
        }
    }

}
