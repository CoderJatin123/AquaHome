package com.application.aquahome.adapter

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.application.aquahome.R
import com.application.aquahome.manager.BTUtils
import com.application.aquahome.manager.HCSensorManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class BluetoothDeviceAdapter(val sensorManager :HCSensorManager, val onSuccessfulConnection: (BluetoothSocket)->Unit): RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder>() {
    val deviceList  = ArrayList<BluetoothDevice>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: MaterialTextView = itemView.findViewById(R.id.bd_title)
        val connectText: MaterialTextView = itemView.findViewById(R.id.txtConnect)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate( R.layout.item_blutooth,parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  deviceList.size
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = deviceList[position]
        try {
            holder.title.text = device.name
        }catch (e: SecurityException){ }

        holder.itemView.setOnClickListener {

            if (!sensorManager.isConnected) {
                sensorManager.connect(device, Onsuccess = {
                    holder.connectText.text = "Connected"
                    sensorManager.socket?.let { it1 -> onSuccessfulConnection(it1) }
                }, failed = {
                    holder.connectText.text = it
                })
            }else{
                sensorManager.disconnect()
                holder.connectText.text = "Disonnected"
            }
        }
    }

    fun update(list : List<BluetoothDevice>){
        deviceList.clear()
        deviceList.addAll(list)
        notifyDataSetChanged()
    }
}