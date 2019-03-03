package com.simprints.simprintsidtester

import android.app.Application
import com.simprints.simprintsidtester.di.KoinCoreModule
import org.koin.android.ext.android.startKoin

class SimprintsApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(KoinCoreModule().appModule))
    }
}