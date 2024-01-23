import dev.zawadzki.samplekmpapplication.Greeting

@JsExport
@OptIn(ExperimentalJsExport::class)
fun getGreeting(): String = Greeting().greet()
