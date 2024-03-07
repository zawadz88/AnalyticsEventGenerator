package dev.zawadzki.samplekmpapplication.android

import android.app.Application
import dev.zawadzki.samplekmpapplication.di.SharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // configure other modules in here if needed
        startKoin {
            androidContext(this@MainApplication)
            androidLogger()
            modules(SharedModule().module)
        }
    }
}
