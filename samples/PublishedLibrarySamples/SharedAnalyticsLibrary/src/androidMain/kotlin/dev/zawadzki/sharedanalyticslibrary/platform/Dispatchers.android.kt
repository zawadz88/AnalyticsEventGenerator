package dev.zawadzki.sharedanalyticslibrary.platform

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val IoDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO
