package com.medcore.mobile

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MedCoreApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialization code if needed
    }
}
