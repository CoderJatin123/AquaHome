package com.application.aquahome.util

import android.bluetooth.BluetoothDevice
import com.google.gson.Gson

object BluetoothDeviceConverter {

    private val gson = Gson()

    fun BluetoothDeviceToJson(device: BluetoothDevice): String {
        return gson.toJson(device)
    }

    fun StringToBluetoothDevice(s: String): BluetoothDevice {
        return gson.fromJson(s, BluetoothDevice::class.java)
    }
}