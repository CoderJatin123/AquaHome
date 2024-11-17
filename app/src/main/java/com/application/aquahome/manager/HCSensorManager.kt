package com.application.aquahome.manager

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.nio.charset.Charset

class HCSensorManager {

    var isConnected= false
    var socket: BluetoothSocket? =null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun connect(device: BluetoothDevice, Onsuccess:()->Unit, failed: (String)->Unit){
        if(!isConnected) {
            BTUtils.connectDevice(device, success = {
                socket = it

                BTUtils.handShake(socket!!, success={
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

    fun startListening(){

    }

    fun sendMessage(message: String, success: () -> Unit, failed: (String) -> Unit){
        if(isConnected){
            try {
            socket?.outputStream?.write(message.toByteArray())

                success()
            }catch (e: Exception){
                e.localizedMessage?.let { failed(it) }
            }
        }
    }

    fun receiveMessage(){
        if(isConnected && socket!=null){
        if(socket?.inputStream?.available()!! >0){
            val x = byteArrayOf()
            socket!!.inputStream.read(x)
//            Toast.makeText(baseContext, ""+x.toString(Charset.defaultCharset()), Toast.LENGTH_SHORT).show()
            socket!!.inputStream.reset()

            Log.d("TAG", "receiveMessage: ${x.toString(Charset.defaultCharset())}")
            }
        }
    }

    fun disconnect(){
        socket?.close()
        socket=null
        isConnected=false
    }
}