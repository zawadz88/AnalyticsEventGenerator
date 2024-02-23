package dev.zawadzki.samplekmpapplication.platform

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val IoDispatcher: CoroutineDispatcher
    get() = Dispatchers.Default
