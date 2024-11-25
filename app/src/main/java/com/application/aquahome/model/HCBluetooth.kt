package com.application.aquahome.model

import android.bluetooth.BluetoothDevice
import com.application.aquahome.util.BluetoothDeviceSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable


@Serializable
data class HCBluetooth(
    @Contextual @Serializable(with = BluetoothDeviceSerializer::class) val sensor: BluetoothDevice, val type: String = "HC05"
)