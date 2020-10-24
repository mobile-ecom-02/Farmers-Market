package com.ilatyphi95.farmersmarket

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.multidex.MultiDex
import androidx.navigation.NavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.ilatyphi95.farmersmarket.utils.NetworkAvailabilityUtils


class MainActivity : AppCompatActivity() {
    companion object{
        private var navView : BottomNavigationView? = null
        private var navController: NavController? = null
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        MultiDex.install(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        navView = findViewById(R.id.nav_view)
//
//        navController = findNavController(R.id.nav_host_fragment)

        NetworkAvailabilityUtils.setNetworkAvailabilityListener(this){

        }

    }


    //Checks if user is logged in
    private fun verifyUserLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid

        if(uid == null){
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController

        }else{
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_message, R.id.navigation_settings))

//        setupActionBarWithNavController(navController, appBarConfiguration)
            navView?.setupWithNavController(navController!!)
        }
    }
}