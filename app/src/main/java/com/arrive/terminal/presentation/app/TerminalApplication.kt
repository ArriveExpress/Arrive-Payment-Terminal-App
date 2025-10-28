package com.arrive.terminal.presentation.app;

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TerminalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initFirebase()
    }
}

private fun initFirebase() {
    FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true
}