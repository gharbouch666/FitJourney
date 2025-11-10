package com.bis5.fitjourney

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bis5.fitjourney.R
import com.bis5.fitjourney.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // This is the only line of code needed. 
        // It correctly sets up the BottomNavigationView to support multiple back stacks,
        // allowing you to switch tabs without getting "stuck".
        binding.bottomNavigation.setupWithNavController(navController)
    }
}
