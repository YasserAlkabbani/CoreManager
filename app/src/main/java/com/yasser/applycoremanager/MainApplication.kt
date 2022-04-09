package com.yasser.applycoremanager

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication:Application() {
    override fun onCreate() {
        super.onCreate()
    }
}