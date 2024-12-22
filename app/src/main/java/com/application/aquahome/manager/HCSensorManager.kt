package com.application.aquahome.manager

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HCSensorManager() {

    var isConnected= false
    var socket: BluetoothSocket? =null
    val socketListener = SocketListener()
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)

    fun connect(device: BluetoothDevice, Onsuccess:()->Unit, failed: (String)->Unit){
        if(!isConnected) {
            connectDevice(device, success = {
                socket = it
                Log.d("TAG", "connect: device is connected.")

                handler.postDelayed({
                        Log.d("TAG", "handshake initiated.")
                    handShake(socket!!, success={
                        isConnected = true
                        socketListener.isConnected=true
                        Onsuccess()
                    }, failed={
                        Log.d("TAG", "connect: f1")
                        failed("Device not recognized")
                        isConnected=false
                    })
                }, 1000)
            }, failed = {

                Log.d("TAG", "connect: f2")
                isConnected = false
                socketListener.isConnected=false
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

                    Log.d("TAG", "getWaterLevel: $value")
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
                        val receivedMessage = String(buffer, 0, bytes, Charsets.UTF_8).trim()
                        val normalizedMessage = receivedMessage.trim()
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
                    }else{
                        Log.d("TAG", "handShake: i am waiting")
                    }
                }
                handler.postDelayed({
                    if (!isReceived) {
                        isReceived=true
                        failed()
                    }
                }, 5000)


            }catch (e : Exception) {
                e.printStackTrace()
                failed()
            }
        }
    }
    class SocketListener(){
        var isConnected = false
        var isTaskCompleted = false
        private val executer: ExecutorService = Executors.newSingleThreadExecutor()

        fun startListening(socket: BluetoothSocket, onOverFlow:()->Unit,failed: (String) -> Unit){
            val buffer = ByteArray(1024)
            var bytes: Int
            executer.execute {
                run {
                    try {
                        socket.outputStream?.write("LISTEN_FOR_OVERFLOW".toByteArray())
                        if(!isConnected){
                            failed("Sensor not connected")
//                            isTaskCompleted=true
                        }
                        isTaskCompleted = false
                        Log.d("TAG", "startListenForOverflow: VM $isTaskCompleted $isConnected")
                        while (isConnected && !isTaskCompleted) {
                            Log.d("TAG", "I am listening: ")
                            if (socket.inputStream.available() > 0) {
                                bytes = socket.inputStream.read(buffer)
                                val receivedMessage = String(buffer, 0, bytes, Charsets.UTF_8).trim()
                                val normalizedMessage = receivedMessage.trim()
                                 if(normalizedMessage=="WATER_OVERFLOW")
                                     onOverFlow()
                                 else
//                                     failed("Some error occurred with sensor")
                                 isTaskCompleted=true
                            }
                        }
                    }catch (e:Exception){
                        isTaskCompleted=true
                        failed(e.printStackTrace().toString())
                    }
                }
            }
        }
    }
}