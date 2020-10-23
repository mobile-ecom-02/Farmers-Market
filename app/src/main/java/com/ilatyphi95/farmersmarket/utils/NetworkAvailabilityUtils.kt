package com.ilatyphi95.farmersmarket.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build


object NetworkAvailabilityUtils {

    private lateinit var networkAvailabilityListener: (Boolean) -> Unit

    private fun isNetworkAvailable(context: Context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val builder = NetworkRequest.Builder()
            connectivityManager.registerNetworkCallback(builder.build(),
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        networkAvailabilityListener(true)
                    }

                    override fun onLost(network: Network) {
                        super.onLost(network)
                        networkAvailabilityListener(false)
                    }
                })


        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkAvailabilityListener(networkInfo != null && networkInfo.isConnected)
        }
    }

    fun setNetworkAvailabilityListener(
        context: Context,
        networkAvailabilityListener: (isConnected: Boolean) -> Unit
    ) {
        isNetworkAvailable(context)
        this.networkAvailabilityListener = networkAvailabilityListener
    }
}