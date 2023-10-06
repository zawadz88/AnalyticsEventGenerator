package dev.zawadzki.analyticseventgenerator.plugin

internal fun readLine(filename: String) = object {}.javaClass.getResourceAsStream(filename)?.use {
    it.bufferedReader().readText()
} ?: ""
