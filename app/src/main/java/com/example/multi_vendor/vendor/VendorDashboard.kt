package com.example.multi_vendor.vendor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.multi_vendor.R
import com.example.multi_vendor.databinding.ActivityVendorDashboardBinding

class VendorDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityVendorDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVendorDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navBottom.setupWithNavController(navController)

    }
}