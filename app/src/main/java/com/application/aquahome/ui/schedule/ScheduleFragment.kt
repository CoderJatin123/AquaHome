package com.application.aquahome.ui.schedule

import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.aquahome.MainActivity
import com.application.aquahome.MyApplication
import com.application.aquahome.adapter.ScheduledTimeStampAdapter
import com.application.aquahome.databinding.FragmentScheduleBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val toolsViewModel = ToolsViewModel(((activity as MainActivity).application as MyApplication).localStorageManager, ((activity as MainActivity).application as MyApplication).workerManager)
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        val adapter = context?.let {
            ScheduledTimeStampAdapter(it, deleteWorker = {key->
                toolsViewModel.deleteWorker(key)
            })
        }

        toolsViewModel.scheduledTimeStamps.observe(viewLifecycleOwner){
            if(it.size>0){
                binding.recyclerView.visibility=View.VISIBLE
                binding.errorMsg.visibility=View.GONE
            }else{
                binding.recyclerView.visibility=View.GONE
                binding.errorMsg.visibility=View.VISIBLE
            }
            adapter?.update(it)
        }

        binding.recyclerView.let{
            it.layoutManager=LinearLayoutManager(context)
            it.adapter= adapter
        }

        val root: View = binding.root
        val timePickerDialog = TimePickerDialog(context,
            { _, h, m ->
//                Toast.makeText(context, "$h $m", Toast.LENGTH_SHORT).show()
                toolsViewModel.addWorker(h,m)
            },hour,minute,false)

        binding.addTimeBtn.setOnClickListener {
            timePickerDialog.show()
        }

        lifecycleScope.launch {
            toolsViewModel.msgFlow.collectLatest {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }


        return root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}