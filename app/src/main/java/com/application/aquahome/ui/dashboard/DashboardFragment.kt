package com.application.aquahome.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.application.aquahome.MainActivity
import com.application.aquahome.MyApplication
import com.application.aquahome.R
import com.application.aquahome.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]
        dashboardViewModel.initialize(((activity as MainActivity).application as MyApplication).localStorageManager)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val lineChart = binding.lineChart.apply {
            this.labelsSize=40f
            this.pointsDrawableRes= R.drawable.ic_dot
        }
        dashboardViewModel.waterLevelList.observe(viewLifecycleOwner){
        lineChart.animate(it)
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}