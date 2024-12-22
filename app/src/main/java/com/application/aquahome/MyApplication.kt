package com.application.aquahome

import android.app.Application
import com.application.aquahome.factory.SensorViewModelFactory
import com.application.aquahome.manager.AppNotificationManager
import com.application.aquahome.manager.HCSensorManager
import com.application.aquahome.manager.LocalStorageManager
import com.application.aquahome.manager.MyWorkerManager
import com.application.aquahome.viewmodel.SensorViewModel

class MyApplication(): Application() {

    val notificationManager by lazy { AppNotificationManager(applicationContext) }
    val localStorageManager by lazy { LocalStorageManager(applicationContext) }
    val workerManager by lazy { MyWorkerManager(applicationContext) }
    val sensorManager by lazy { HCSensorManager() }
    val sensorViewModel by lazy { SensorViewModelFactory(localStorageManager,sensorManager,notificationManager).create(SensorViewModel::class.java)}
}