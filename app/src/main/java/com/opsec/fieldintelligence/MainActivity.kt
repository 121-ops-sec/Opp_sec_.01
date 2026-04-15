package com.opsec.fieldintelligence

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.opsec.fieldintelligence.databinding.ActivityMainBinding
import com.opsec.fieldintelligence.location.LocationService
import com.opsec.fieldintelligence.util.PermissionHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        if (granted) {
            LocationService.startService(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)

        checkAndRequestLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Keep service running unless user explicitly stops it
    }

    private fun checkAndRequestLocation() {
        if (PermissionHelper.hasAnyLocation(this)) {
            LocationService.startService(this)
        } else {
            PermissionHelper.requestLocationPermissions(locationPermissionLauncher)
        }
    }
}
