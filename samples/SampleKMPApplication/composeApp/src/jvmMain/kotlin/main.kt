import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.zawadzki.samplekmpapplication.di.SharedModule
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

fun main() {
    // configure other modules in here if needed
    startKoin {
        modules(SharedModule().module)
    }
    application {
        Window(onCloseRequest = ::exitApplication, title = "KotlinProject") {
            App()
        }
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}
