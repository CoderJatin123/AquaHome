package com.application.aquahome.activities

import android.bluetooth.BluetoothManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.aquahome.R
import com.application.aquahome.adapter.BluetoothDeviceAdapter
import com.application.aquahome.databinding.ActivityAddSensorBinding
import com.application.aquahome.manager.HCSensorManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.log

class AddSensorActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddSensorBinding

    val adapter = BluetoothDeviceAdapter(HCSensorManager()){ bluetoothDevice ->

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddSensorBinding.inflate(layoutInflater)
        val viewModel = ViewModelProvider(this)[AddActivityViewModel::class]
        setContentView(binding.root)
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