package com.example.onlineshop.Activities

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.onlineshop.R
import com.example.onlineshop.activity.activity.BaseActivity
//import com.example.onlineshop.activity.activity.databinding.ActivityDashboardBinding
import com.example.onlineshop.databinding.ActivityDashboardBinding

class Dashboard : BaseActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        supportActionBar!!.setBackgroundDrawable(ContextCompat.getDrawable(this@Dashboard,R.drawable.app_gradient_color_background))
        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_products,
            R.id.navigation_dashboard,
            R.id.navigation_orders,
            R.id.navigation_sold_products
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }
}