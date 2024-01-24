package dev.zawadzki.sharedanalyticslibrary

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform