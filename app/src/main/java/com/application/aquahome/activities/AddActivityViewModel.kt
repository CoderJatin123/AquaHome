package com.application.aquahome.activities

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.aquahome.manager.BTUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class AddActivityViewModel(): ViewModel() {

    val messageFlow = MutableSharedFlow<String>()
    private val _deviceList = MutableLiveData<List<BluetoothDevice>>().apply { this.value= arrayListOf() }
    val deviceList : LiveData<List<BluetoothDevice>> = _deviceList

    fun updateList(manager: BluetoothManager){
        BTUtils.getDeviceList(manager, success = {
            _deviceList.value=it
        }, failed = {
            generateMessage(it)
        })
    }

    private fun generateMessage(msg: String){
        viewModelScope.launch {
            messageFlow.emit(msg)
        }
    }
}