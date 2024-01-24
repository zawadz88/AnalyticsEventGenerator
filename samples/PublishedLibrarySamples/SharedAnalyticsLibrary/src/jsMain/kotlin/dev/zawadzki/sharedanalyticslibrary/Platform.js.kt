package dev.zawadzki.sharedanalyticslibrary

class JsPlatform : Platform {
    override val name: String = "JS"
}

actual fun getPlatform(): Platform = JsPlatform()
