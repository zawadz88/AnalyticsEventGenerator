package dev.zawadzki.analyticseventgenerator.plugin

import java.util.Locale

private val SNAKE_CASE_PATTERN = "_[a-z]".toRegex()

internal fun String.toClassName(): String = removeWhiteSpaces()
    .snakeToCamelCase()
    .capitalizeFirstChar()

internal fun String.toPropertyName(): String = removeWhiteSpaces()
    .snakeToCamelCase()

internal fun String.toEnumConstantName(): String = removeWhiteSpaces()
    .camelToSnakeCase()
    .uppercase()

internal fun String.removeWhiteSpaces() = this.replace("\\p{Zs}+".toRegex(), "")

internal fun String.capitalizeFirstChar() = this.replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}

internal fun String.snakeToCamelCase(): String =
    replace(SNAKE_CASE_PATTERN) { it.value.last().uppercase() }

internal fun String.camelToSnakeCase(): String {
    val pattern = "(?<=.)[A-Z]".toRegex()
    return this.replace(pattern, "_$0").lowercase()
}
