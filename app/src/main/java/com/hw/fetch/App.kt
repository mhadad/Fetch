package com.hw.fetch

import android.app.Application
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp()
class App  : Application() {
    override fun onCreate() {
        super.onCreate()
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .build())
    }
}