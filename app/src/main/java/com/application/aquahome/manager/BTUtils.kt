package com.application.aquahome.manager

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.UUID
import kotlin.math.log

object BTUtils {
    private val handler = Handler(Looper.getMainLooper())
    fun connectDevice(device: BluetoothDevice,success:(BluetoothSocket)->Unit, failed: (String)->Unit) {
        try {
//            00001101-0000-1000-8000-00805F9B34FB
            val myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            val d = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device.address)
            val socket = d.createRfcommSocketToServiceRecord(myUUID)
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
            socket.connect()
            success(socket)
        }
        catch (e: SecurityException){
            e.localizedMessage?.let { failed(it) }
        }catch (e: Exception){
            e.localizedMessage?.let { failed(it) }
        }
    }

    fun getDeviceList(bluetoothManager: BluetoothManager, success: (List<BluetoothDevice>) -> Unit, failed: (String)->Unit) {
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
        if (bluetoothAdapter != null) {
            try {
                val pairedDevices: List<BluetoothDevice> = bluetoothAdapter.bondedDevices.toList()
                success(pairedDevices)
            }catch(e: SecurityException){
               failed("Bluetooth permission not granted")
            }
        } else {
            failed("Bluetooth device not supported")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun handShake(socket: BluetoothSocket, success:()->Unit, failed: () -> Unit){
        var isSent=false
        var isReceived= false


        val buffer = ByteArray(1024) // buffer to hold the incoming data
        var bytes: Int // number of bytes read
        try {
            socket.outputStream?.write("HELLO".toByteArray())
            while (true) {
                if (socket.inputStream.available() > 0) {
                    bytes=socket.inputStream.read(buffer)
                    val receivedMessage = String(buffer, 0, bytes)
                        socket.inputStream.reset()
                    if(receivedMessage=="HELLOG") {
                        isReceived = true
                        Log.d("TAG", "handShake: S")
                        success()

                    }else
                    Log.d("TAG", "handShake: F1")
                        failed()
//                    [B@684f7b6
                }
            }
            handler.postDelayed({
                if (!isReceived) {
                    Log.d("TAG", "handShake: F2")
                    failed()
                }
            }, 10000)


        }catch (e : Exception) {
            failed()
        }
    }
}