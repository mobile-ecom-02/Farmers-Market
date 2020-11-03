package com.ilatyphi95.farmersmarket.utils

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

const val CHECK_LOCATION_SETTING = 10001

class LocationUtils(val context: FragmentActivity,
                    private val locationCallback: LocationCallback) : LifecycleObserver {

    private val fusedLocationClient : FusedLocationProviderClient
            = LocationServices.getFusedLocationProviderClient(context)

    companion object {
        fun  checkLocationRequest(context: FragmentActivity, successCase : () -> Unit) {
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(createLocationRequest())

            val client: SettingsClient = LocationServices.getSettingsClient(context)
            val task = client.checkLocationSettings(
                builder.build()
            )

            task.addOnSuccessListener {
                successCase()
            }

            task.addOnFailureListener { exception ->
                if(exception is ResolvableApiException) {
                    try {
                        exception.startResolutionForResult(context, CHECK_LOCATION_SETTING)
                    } catch (sendEx: IntentSender.SendIntentException) {

                    }
                }
            }
        }

        fun getLastLocation(context: FragmentActivity) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            LocationServices.getFusedLocationProviderClient(context)
                .lastLocation.addOnSuccessListener { location ->

            }
        }

        private fun createLocationRequest(): LocationRequest {

            return LocationRequest().apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback,
        Looper.getMainLooper())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}