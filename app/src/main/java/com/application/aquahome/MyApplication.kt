package com.application.aquahome

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.work.Configuration
import androidx.work.WorkManager
import com.application.aquahome.factory.SensorViewModelFactory
import com.application.aquahome.manager.HCSensorManager
import com.application.aquahome.manager.LocalStorageManager
import com.application.aquahome.manager.MyWorkerManager
import com.application.aquahome.viewmodel.SensorViewModel

class MyApplication(): Application() {

    val localStorageManager by lazy { LocalStorageManager(applicationContext) }
    val workerManager by lazy { MyWorkerManager(applicationContext) }
    val sensorManager by lazy { HCSensorManager() }
    val sensorViewModel by lazy { SensorViewModelFactory(localStorageManager,sensorManager).create(SensorViewModel::class.java)}

    override fun onCreate() {
        super.onCreate()
    }
}