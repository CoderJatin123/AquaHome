package com.application.aquahome

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.application.aquahome.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.Manifest
import com.karumi.dexter.listener.PermissionRequest


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_schedule
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        lifecycleScope.launch {
            (application as MyApplication).sensorViewModel.msgFlow.collectLatest {
                Snackbar.make(binding.root,it,Snackbar.LENGTH_LONG).show()
            }
            (application as MyApplication).sensorViewModel.connectionFlow.collectLatest {
                Snackbar.make(binding.root,it.toString(),Snackbar.LENGTH_LONG).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        checkPermission()

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkPermission() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        // All permissions are granted
                        (application as MyApplication).sensorViewModel.initialize()

                    } else {
                        // Handle denied permissions
                        if (report.isAnyPermissionPermanentlyDenied) {
                           showSnackbar("Permissions are permanently denied. Please enable them in settings to proceed.")
                        } else {
                            showSnackbar("Some permissions are denied. Please grant them to continue.")
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }


    fun showSnackbar(msg: String){
        Snackbar.make(binding.root,msg,Snackbar.LENGTH_LONG).show()
    }

}