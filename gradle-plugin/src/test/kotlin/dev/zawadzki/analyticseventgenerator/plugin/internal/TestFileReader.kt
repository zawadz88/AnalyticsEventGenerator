package dev.zawadzki.analyticseventgenerator.plugin.internal

internal fun readLine(filename: String) = object {}.javaClass.getResourceAsStream(filename)?.use {
    it.bufferedReader().readText()
} ?: ""
