package com.application.aquahome.manager

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.hardware.Sensor
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.application.aquahome.util.CurrentBTDevice
import java.nio.charset.Charset
import java.util.UUID

class HCSensorManager {

    var isConnected= false
    var socket: BluetoothSocket? =null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun connect(device: BluetoothDevice, Onsuccess:()->Unit, failed: (String)->Unit){
        if(!isConnected) {
            connectDevice(device, success = {
                socket = it
                handShake(socket!!, success={
                    isConnected = true
                    Onsuccess()
                }, failed={
                    failed("Device not recognized")
                    isConnected=false
                })

            }, failed = {
                isConnected = false
                socket = null
                failed(it)
            })
        }else{
            Onsuccess()
        }
    }

    fun getWaterLevel(sensor: BluetoothDevice, data:(Int)->Unit, failed: (String) -> Unit){
        var isReceived= false
        val buffer = ByteArray(1024)
        var bytes: Int
        try {
            socket!!.outputStream?.write("GET_WATER_LEVEL".toByteArray())
            while (!isReceived) {
                if (socket!!.inputStream.available() > 0) {
                    bytes=socket!!.inputStream.read(buffer)
                    val receivedMessage = String(buffer, 0, bytes)

                    val value = receivedMessage.trim().toIntOrNull()
                    if (value != null && value!=-1) {
                        data(value)
                    } else {
                        data(-1)
                    }

                    isReceived=true
                    break
                }
            }

            handler.postDelayed({
                if (!isReceived) {
                    isReceived=true
                    failed("Sensor not responding")
                }
            }, 3000)


        }catch (e : Exception) {
            e.printStackTrace()
            failed("failed to connect")
        }

    }


    fun disconnect(){
        socket?.close()
        socket=null
        isConnected=false
    }

    companion object{
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
                e.printStackTrace()
                e.localizedMessage?.let { failed(it) }
            }catch (e: Exception){
                e.printStackTrace()
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
            var isReceived= false
            val buffer = ByteArray(1024)
            var bytes: Int
            try {
                socket.outputStream?.write("HELLO".toByteArray())
                while (!isReceived) {
                    if (socket.inputStream.available() > 0) {
                        bytes=socket.inputStream.read(buffer)
                        val receivedMessage = String(buffer, 0, bytes)
                        val normalizedMessage = receivedMessage.trim()
//                     socket.inputStream.reset()
                        if (normalizedMessage == "HELLOG") {
                            success()
                            isReceived = true
//                        Log.d("TAG", "handShake: S")
                        } else {
//                        Log.d("TAG", "handShake: F1")
                            failed()
                        }
                        isReceived=true
                        break
                    }
                }

                handler.postDelayed({
                    if (!isReceived) {
                        isReceived=true
                        failed()
                    }
                }, 3000)


            }catch (e : Exception) {
                e.printStackTrace()
                failed()
            }
        }
    }
}