package com.application.aquahome.ui.home

import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.application.aquahome.MainActivity
import com.application.aquahome.MyApplication
import com.application.aquahome.R
import com.application.aquahome.activities.AddSensorActivity
import com.application.aquahome.databinding.FragmentHomeBinding
import com.application.aquahome.util.SensorStatus
import com.application.aquahome.viewmodel.SensorViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val viewModel = ((activity as MainActivity).application as MyApplication).sensorViewModel
        viewModel.sensorStatus.observe(viewLifecycleOwner){ it ->
            when(it){
                SensorStatus.NOT_SELECTED -> {
                    binding.let {
                        it.deviceInfo.text="Unknown device"
                        it.deviceStatus.text="No device selected"
                        it.btnAddDevice.text="Add device"
                    }
                }
                SensorStatus.CONNECTED -> {
                    binding.let {
                        it.deviceInfo.text= "Device Name : ${viewModel.deviceName}"
                        it.deviceStatus.text="Connected"
                        it.btIcon.setImageResource(R.drawable.ic_bt_connected)
                        it.btnAddDevice.text="Disconnect"
                    }
                }
                SensorStatus.DISCONNECTED -> {
                    binding.let {
                        it.deviceStatus.text="Disconnected"
                        it.deviceInfo.text= "Device Name : ${viewModel.deviceName}"
                        it.btnAddDevice.text="Connect"
                        it.btIcon.setImageResource(R.drawable.ic_bt)
                    }
                }
                SensorStatus.CONNECTING -> {
                    Log.d("TAG", "onCreateView: connecting")
                    binding.let {
                        it.deviceStatus.text="Connecting..."
                        it.deviceInfo.text= "Device Name : ${viewModel.deviceName}"
                    }
                }
            }
        }

        viewModel.waterLevel.observe(viewLifecycleOwner){
            binding.waterLevel.text = "${it*10}%"
        }

        binding.btnAddDevice.setOnClickListener {
             if(viewModel.sensorStatus.value==SensorStatus.CONNECTED){
                binding.deviceStatus.text="Disconnecting..."
            }
            viewModel.onAddBtnClick(openAddSensorActivity = {
                startActivity(Intent(context,AddSensorActivity::class.java))
            })

        }

        binding.refreshBtn.setOnClickListener {
            binding.waterLevel.text="-- %"
            viewModel.updateWaterLevel()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}