package com.application.aquahome

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.application.aquahome.manager.LocalStorageManager
import com.application.aquahome.manager.MyWorkerManager

class MyApplication(): Application() {

    val localStorageManager by lazy { LocalStorageManager(applicationContext) }
    val workerManager by lazy { MyWorkerManager(applicationContext) }

    override fun onCreate() {
        super.onCreate()
    }
}