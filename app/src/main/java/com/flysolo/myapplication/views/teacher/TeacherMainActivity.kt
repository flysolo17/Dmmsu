package com.flysolo.myapplication.views.teacher

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.flysolo.myapplication.R
import com.flysolo.myapplication.databinding.ActivityTeacherMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class TeacherMainActivity : AppCompatActivity() {
    private lateinit var navController : NavController
    private lateinit var binding : ActivityTeacherMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.elevation = 1f

        val navView: BottomNavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_container)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_scoreboard,
                R.id.navigation_account
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _: NavController?, destination: NavDestination, _: Bundle? ->
            when (destination.id) {
                R.id.navigation_home -> {
                    showBottomNav()
                }
                R.id.navigation_scoreboard -> {
                    showBottomNav()
                }
                R.id.navigation_account -> {
                    showBottomNav()
                }
                else -> {
                    hideBottomNav()
                }
            }
        }
    }
    private fun showBottomNav() {
        binding.bottomAppBar.performShow(true)
        binding.bottomAppBar.hideOnScroll = true

    }

    private fun hideBottomNav() {
        binding.bottomAppBar.performHide(true)
        binding.bottomAppBar.hideOnScroll = false
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}