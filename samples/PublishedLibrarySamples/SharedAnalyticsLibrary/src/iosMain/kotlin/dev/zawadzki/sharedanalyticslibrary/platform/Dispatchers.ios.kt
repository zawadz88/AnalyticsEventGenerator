package dev.zawadzki.sharedanalyticslibrary.platform

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal actual val IoDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO
