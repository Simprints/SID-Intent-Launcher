package com.simprints.simprintsidtester

import android.app.Application
import com.simprints.simprintsidtester.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class SimprintsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@SimprintsApplication)
            modules(listOf(appModule))
        }
    }
}