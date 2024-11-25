package com.application.aquahome.activities

import android.bluetooth.BluetoothManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.aquahome.MyApplication
import com.application.aquahome.adapter.BluetoothDeviceAdapter
import com.application.aquahome.databinding.ActivityAddSensorBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddSensorActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddSensorBinding



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddSensorBinding.inflate(layoutInflater)
        val viewModel = ViewModelProvider(this)[AddActivityViewModel::class]
        setContentView(binding.root)
        val adapter = BluetoothDeviceAdapter((application as MyApplication).sensorManager){ device->

            (application as MyApplication).let {
                it.sensorViewModel.onSensorConnected(device)
            }
            binding.btnDone.isEnabled=true

        }


        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        binding.recyclerView.adapter=adapter

        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        viewModel.updateList(bluetoothManager)

        viewModel.deviceList.observe(this){
            if(it.isEmpty()){
                binding.recyclerView.visibility=View.GONE
                binding.textMessage.text="No paired bluetooth device availble"
            }else{
                binding.recyclerView.visibility=View.VISIBLE
                binding.textMessage.visibility=View.GONE
                adapter.update(it)
            }
        }

        lifecycleScope.launch {
            viewModel.messageFlow.collectLatest {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}