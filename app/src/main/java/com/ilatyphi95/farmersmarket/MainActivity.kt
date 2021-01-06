package com.ilatyphi95.farmersmarket

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.multidex.MultiDex
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.api.LogDescriptor
import com.google.firebase.auth.FirebaseAuth
import com.ilatyphi95.farmersmarket.data.repository.MESSAGE_NOTIFICATION_CHANNEL_ID
import com.ilatyphi95.farmersmarket.utils.NetworkAvailabilityUtils


class MainActivity : AppCompatActivity() {
    companion object{
        private var navView : BottomNavigationView? = null
        private var navController: NavController? = null
    }

    val tag = "MainActivity"

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        MultiDex.install(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

//        navView = findViewById(R.id.nav_view)
//
//        navController = findNavController(R.id.nav_host_fragment)

        NetworkAvailabilityUtils.setNetworkAvailabilityListener(this){

        }

    }

    override fun onStop() {
        Log.d(tag, "onStop: OnStopped Called")
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
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

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(MESSAGE_NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}