package dev.zawadzki.samplekmpapplication

class WasmJsPlatform : Platform {
    override val name: String = "Web"
}

actual fun getPlatform(): Platform = WasmJsPlatform()
