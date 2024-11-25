package com.application.aquahome.viewmodel

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.aquahome.manager.HCSensorManager
import com.application.aquahome.manager.LocalStorageManager
import com.application.aquahome.util.SensorStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class SensorViewModel(private val storageManager: LocalStorageManager, private val sensorManager: HCSensorManager) : ViewModel()  {
    private val _msgFlow = MutableSharedFlow<String>()
    val msgFlow: SharedFlow<String> = _msgFlow
    private val _waterLevel = MutableLiveData<Int>().apply { this.value = 0 }
     val waterLevel : LiveData<Int> =_waterLevel

    private val _connectionFlow = MutableSharedFlow<SensorStatus>()
    val connectionFlow : SharedFlow<SensorStatus> = _connectionFlow

    var deviceName=""
    var device:BluetoothDevice?=null
    private val _sensorStatus = MutableLiveData<SensorStatus>().apply {
        this.value=SensorStatus.DISCONNECTED
    }
    val sensorStatus: LiveData<SensorStatus> = _sensorStatus

    fun initialize(){
        checkPreviousSensor()
    }

    private fun checkPreviousSensor(){
            storageManager.getSensor(device = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    this.device=it
                    reConnect(it)
                }
            }, fail = {
                _sensorStatus.value = SensorStatus.NOT_SELECTED
            })
    }

    fun onSensorConnected(device: BluetoothDevice) {



        this.device= device
        saveSensor()
        updateName(device)
        _sensorStatus.value=SensorStatus.CONNECTED
        updateWaterLevel()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun reConnect(sensor: BluetoothDevice) {
        Log.d("TAG", "reConnect: ")

        if (!isBluetoothEnabled()) {
            createMsg("Bluetooth is turned off. Please turn it on and try again!")
            return
        }

        _sensorStatus.value = SensorStatus.CONNECTING

        // Perform connection on a background thread
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Wrap callback-based API to suspend function
                val isConnected = suspendCoroutine<Boolean> { continuation ->
                    sensorManager.connect(sensor,
                        Onsuccess = { continuation.resume(true) },
                        failed = { continuation.resume(false) }
                    )
                }

                // Switch to the main thread to handle success or failure
                withContext(Dispatchers.Main) {
                    if (isConnected) {
                        this@SensorViewModel.device = sensor
                        onSensorConnected(sensor)
                    } else {
                        createMsg("Failed to reconnect.")
                        _sensorStatus.value = SensorStatus.DISCONNECTED
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    createMsg("An error occurred: ${e.message}")
                    _sensorStatus.value = SensorStatus.DISCONNECTED
                }
            }
        }
    }

     fun updateWaterLevel(){

        if(device==null)
            return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Wrap callback-based API to suspend function
                val data = suspendCoroutine<Int> { continuation ->

                    sensorManager.getWaterLevel(device!!, data = {
                        continuation.resume(it)
                    }, failed = {
                        continuation.resume(-1)
                    } )

                }

                // Switch to the main thread to handle success or failure
                withContext(Dispatchers.Main) {
                    if (data != -1) {
                        _waterLevel.value=data
                    } else {
                        createMsg("Some thing went wrong with sensor.")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    createMsg("An error occurred: ${e.message}")
                    _sensorStatus.value = SensorStatus.DISCONNECTED
                }
            }
        }
    }

    private fun updateName(device: BluetoothDevice){
            try{
            this.deviceName = device.name
            }
            catch (e: SecurityException){
                this.deviceName="Unknown"
            }
    }

    private fun saveSensor(){
            try{
                if (this.device!=null)
                    storageManager.addSensor(device!!, success = {
                }, fail = {
                    createMsg("Unable to add sensor")
                })
            }
            catch (e: SecurityException){
                this.deviceName="Unable to add sensor"
            }
    }

    private fun disconnect(){
        sensorManager.disconnect()
        _sensorStatus.value=SensorStatus.DISCONNECTED
    }

    private fun createMsg(msg: String){
        viewModelScope.launch {
            _msgFlow.emit(msg)
        }
    }

    fun onAddBtnClick(openAddSensorActivity:()->Unit) {
        Log.d("TAG", "onAddBtnClick: $device")
        when (sensorStatus.value) {
            SensorStatus.NOT_SELECTED -> openAddSensorActivity()
            SensorStatus.CONNECTED -> disconnect()
            SensorStatus.DISCONNECTED -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                device?.let { reConnect(it) }
            }

            SensorStatus.CONNECTING -> {

            }

            else -> {

            }
        }
    }
    fun isBluetoothEnabled(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val isEnabled = bluetoothAdapter != null && bluetoothAdapter.isEnabled
        Log.d("TAG", "Bluetooth Enabled: $isEnabled")
        return isEnabled
    }


}
