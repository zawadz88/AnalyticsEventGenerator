package com.example.yamlplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.yamlplayground.ui.theme.SampleAndroidApplicationTheme
import dev.zawadzki.sharedanalyticslibrary.event.SampleAdditionalButtonTapped
import dev.zawadzki.sharedanalyticslibrary.event.SampleButtonTapped
import dev.zawadzki.sharedanalyticslibrary.event.SampleSomething

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleAndroidApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(greet())
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SampleAndroidApplicationTheme {
        Greeting("Android")
    }
}

private fun greet(): String {
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

    return """Hello
            | $sampleSomethingEvent
            | $sampleButtonTapped
            | $sampleAdditionalButtonTapped
        """.trimMargin()
}
