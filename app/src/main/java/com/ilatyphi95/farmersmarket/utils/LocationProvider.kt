package com.ilatyphi95.farmersmarket.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


// returns the address in format long|lat|city|LGA|state|country
const val SEPARATOR = "|"

class LocationProvider(
    val context: FragmentActivity, val operationResult: (Boolean, String) -> Unit
) {
    private var fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var settingsClient: SettingsClient = LocationServices.getSettingsClient(context)
    private var locationRequest: LocationRequest
    private var locationSettingRequest: LocationSettingsRequest
    private var locationCallback: LocationCallback

    init {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                locationResult?.let {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val lastLocation = locationResult.lastLocation

                    try {
                        val location = geocoder.getFromLocation(
                            lastLocation.latitude,
                            lastLocation.longitude,
                            1
                        )
                        if (location.size > 0) {
                            val address = location[0]
                            val fullAddress = listOf(
                                lastLocation.latitude,
                                lastLocation.longitude, address.locality, address.subAdminArea,
                                address.adminArea, address.countryName
                            ).joinToString(SEPARATOR)

                            operationResult(true, fullAddress)
                        }
                    } catch (e: IOException) {
                        operationResult(false, e.message ?: "Error Occurred")
                    }

                }
            }
        }

        locationRequest = LocationRequest().apply {

            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            numUpdates = 5
        }

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        locationSettingRequest = builder.build()

        startLocationUpdates()
    }

    private fun startLocationUpdates() {

        settingsClient.checkLocationSettings(locationSettingRequest)
            .addOnSuccessListener(
                context
            ) { locationSettingsResponse ->

                locationSettingsResponse?.let {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return@addOnSuccessListener
                    }
                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback, Looper.myLooper()
                    )
                }
            }
            .addOnFailureListener(context) {
                operationResult(true, it.message ?: "")
            }
    }

    companion object {
        fun isLocationEnabled(context: Context) : Boolean {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
     }
}