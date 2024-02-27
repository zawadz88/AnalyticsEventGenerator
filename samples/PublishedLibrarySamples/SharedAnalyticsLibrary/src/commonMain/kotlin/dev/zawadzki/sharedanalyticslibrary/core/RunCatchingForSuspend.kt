package dev.zawadzki.sharedanalyticslibrary.core

import kotlinx.coroutines.CancellationException

/**
 * Source: https://proandroiddev.com/resilient-use-cases-with-kotlin-result-coroutines-and-annotations-511df10e2e16
 */
@Suppress("TooGenericExceptionCaught")
internal suspend fun <R> suspendRunCatching(block: suspend () -> R): Result<R> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
