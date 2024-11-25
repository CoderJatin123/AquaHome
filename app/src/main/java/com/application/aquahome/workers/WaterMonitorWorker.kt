package com.application.aquahome.workers

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.application.aquahome.manager.AppNotificationManager
import com.application.aquahome.manager.HCSensorManager
import com.application.aquahome.manager.LocalStorageManager
import com.application.aquahome.model.WaterLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class WaterMonitorWorker(val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val notificationManager = AppNotificationManager(context)
        try {
            val storageManager = LocalStorageManager(applicationContext)
            storageManager.getSensor(device = { sensor ->
                val sensorManager = HCSensorManager()
                sensorManager.connect(sensor, Onsuccess = {
                    sensorManager.getWaterLevel(sensor, data = {
                        saveInDatabase(storageManager,it)
                        notificationManager.postNotification("Water Level Check Complete","Your tank's water level has been checked.", "The current water level is ${it*10}%. Please take action if needed.")
                        sensorManager.disconnect()
                    }, failed = {
                        notificationManager.postNotification("Unable to Check Water Level","The system couldn't get a response from your tank.", "Check your Bluetooth or sensor connectivity and ensure everything is properly set up.")
                    })

                }, failed = {
                    notificationManager.postNotification("Unable to Check Water Level", "The app couldn't connect to the water tank sensor.", "Please check if the sensor is powered on and properly connected. Ensure there are no hardware issues and try again.")
                })
            }, fail = {
                //Bluetooth is off
            })
        } catch (e: SecurityException) {
            notificationManager.postNotification("Error Checking Water Level", "The system encountered an error","Suggested actions, e.g., Restart the app or verify your settings." )
            return@withContext Result.retry()
        }
        Log.d("WaterMonitorWorker", "Water monitoring completed successfully.")
        return@withContext Result.success()
    }

    private fun saveInDatabase(storageManager: LocalStorageManager, it: Int) {
       val wl = WaterLevel().apply {
           this.value=it
           this.time=Date().time
       }
        storageManager.updateWaterLevel(wl)
    }

}