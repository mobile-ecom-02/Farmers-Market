package com.ilatyphi95.farmersmarket.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
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

    fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }
}