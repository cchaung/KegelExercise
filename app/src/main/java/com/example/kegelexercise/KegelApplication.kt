package com.example.kegelexercise

import android.app.Application
import com.example.kegelexercise.util.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KegelApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
    }
}
