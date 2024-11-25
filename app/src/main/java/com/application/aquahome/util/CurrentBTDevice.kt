package com.application.aquahome.util

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData

object CurrentBTDevice {

    var device : BluetoothDevice? = null
    var isConnected = MutableLiveData<Boolean>().apply { this.value = false }
}