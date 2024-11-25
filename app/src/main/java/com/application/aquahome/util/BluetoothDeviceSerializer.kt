package com.application.aquahome.util

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object BluetoothDeviceSerializer : KSerializer<BluetoothDevice> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BluetoothDevice", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BluetoothDevice) {
        encoder.encodeString(value.address) // Serialize only the address
    }

    override fun deserialize(decoder: Decoder): BluetoothDevice {
        val address = decoder.decodeString()
        // Replace with a suitable way to recreate the BluetoothDevice instance
        return BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address)
    }
}