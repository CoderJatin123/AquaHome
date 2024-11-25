package com.application.aquahome.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.application.aquahome.manager.LocalStorageManager
import com.application.aquahome.model.WaterLevel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log

class DashboardViewModel : ViewModel() {
    lateinit var storageManager: LocalStorageManager
    private val _waterLevelList = MutableLiveData<List<Pair<String, Float>>>().apply { this.value=
        listOf() }

    val waterLevelList : LiveData<List<Pair<String, Float>>> = _waterLevelList

    fun initialize(localStorageManager: LocalStorageManager){
        this.storageManager=localStorageManager
        updateData()
    }

    private fun updateData(){
        storageManager.getWaterLevels(waterLevels = {
            _waterLevelList.value = formatWaterLevelList(it)
        }, fail = {

        })
    }
    private fun formatWaterLevelList(waterLevels: List<WaterLevel>): List<Pair<String, Float>> {
        val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
        return waterLevels.map {
            val formattedTime = formatter.format(Date(it.time))
            Pair(formattedTime, it.value.toFloat())
        }
    }
}