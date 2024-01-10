package dev.zawadzki.samplekmpapplication

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform