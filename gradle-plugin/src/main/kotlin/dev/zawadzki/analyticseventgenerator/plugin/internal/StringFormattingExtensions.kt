package dev.zawadzki.analyticseventgenerator.plugin.internal

import java.util.Locale

private val SNAKE_CASE_PATTERN = "_[a-z]".toRegex()

internal fun String.toClassName(): String = replaceWhiteSpacesWithUnderscores()
    .snakeToCamelCase()
    .capitalizeFirstChar()

internal fun String.toPropertyName(): String = removeWhiteSpaces()
    .snakeToCamelCase()

internal fun String.toEnumConstantName(): String = replaceWhiteSpacesWithUnderscores()
    .split('_')
    .filter { it.isNotEmpty() }
    .joinToString(separator = "_")
    .uppercase(Locale.US)

internal fun String.removeWhiteSpaces() = this.replace("\\p{Zs}+".toRegex(), "")

internal fun String.replaceWhiteSpacesWithUnderscores() = this.replace("\\p{Zs}+".toRegex(), "_")

internal fun String.capitalizeFirstChar() = this.replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString()
}

internal fun String.snakeToCamelCase(): String =
    replace(SNAKE_CASE_PATTERN) { it.value.last().uppercase(Locale.US) }
