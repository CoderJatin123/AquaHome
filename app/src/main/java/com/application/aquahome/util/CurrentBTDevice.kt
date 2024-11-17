package com.application.aquahome.util

import android.bluetooth.BluetoothDevice

object CurrentBTDevice {
    var device : BluetoothDevice? = null
    var isConnected = false
}