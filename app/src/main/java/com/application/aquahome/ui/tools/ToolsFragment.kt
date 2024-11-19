package com.application.aquahome.ui.tools

import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.application.aquahome.databinding.FragmentToolsBinding

class ToolsFragment : Fragment() {

    private var _binding: FragmentToolsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        val notificationsViewModel =
//            ViewModelProvider(this).get(NotificationsViewModel::class.java)
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        _binding = FragmentToolsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val timePickerDialog = TimePickerDialog(context,
            { view, h, m ->
                Toast.makeText(context, "$h $m", Toast.LENGTH_SHORT).show()

            },hour,minute,false)

        binding.addTimeBtn.setOnClickListener {
            timePickerDialog.show()
        }



        return root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}