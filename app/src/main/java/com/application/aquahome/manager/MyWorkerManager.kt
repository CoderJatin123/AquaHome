package com.application.aquahome.manager

import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.application.aquahome.util.TimeUtil
import com.application.aquahome.workers.WaterMonitorWorker
import java.util.concurrent.TimeUnit

class MyWorkerManager(private val cxt: Context) {
    init {
//        WorkManager.initialize(cxt, Configuration.Builder().build())
    }

    fun scheduleWorker(hour: Int, minute: Int, success: (String,String)->Unit, fail:()->Unit) {
        val currentTime = Calendar.getInstance()
        val selectedTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        // Calculate initial delay
        val delayMillis = if (selectedTime.timeInMillis > currentTime.timeInMillis) {
            selectedTime.timeInMillis - currentTime.timeInMillis
        } else {
            // Add a day if the time has already passed for today
            selectedTime.timeInMillis + (24 * 60 * 60 * 1000) - currentTime.timeInMillis
        }

        // Generate a unique worker key
        val uniqueKey = "DailyWorker_${System.currentTimeMillis()}"
        Log.d("TAG","Generated Worker Key: $uniqueKey")
        Log.d("TAG","Time : ${TimeUtil.formatTimeToReadable(selectedTime.timeInMillis)}")

        try {
            val workRequest = PeriodicWorkRequestBuilder<WaterMonitorWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(cxt).enqueueUniquePeriodicWork(
                uniqueKey,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
            success(uniqueKey,TimeUtil.formatTimeToReadable(selectedTime.timeInMillis))
        } catch (e: Exception) {
            fail()
            Log.e("WorkManagerError", "Failed to enqueue work: ${e.message}")
        }
    }

    fun cancelScheduledWorker(key: String, success: () -> Unit, fail: () -> Unit){
        try {
            WorkManager.getInstance(cxt).cancelUniqueWork(key)
            success()
        }catch (e: Exception){
            fail()
        }
    }
}