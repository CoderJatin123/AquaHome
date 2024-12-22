package com.application.aquahome.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.application.aquahome.manager.AppNotificationManager
import com.application.aquahome.manager.HCSensorManager
import com.application.aquahome.manager.LocalStorageManager
import com.application.aquahome.viewmodel.SensorViewModel

class SensorViewModelFactory(
    private val localStorageManager: LocalStorageManager,
    private val hcSensorManager: HCSensorManager,
    private val notificationManager: AppNotificationManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SensorViewModel::class.java)) {
            return SensorViewModel(localStorageManager,hcSensorManager,notificationManager) as T
       }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


