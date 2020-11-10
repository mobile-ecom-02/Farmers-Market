package com.ilatyphi95.farmersmarket.data.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyLocation @JvmOverloads constructor(
    val accuracy: Float = 3F,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val time: Long = 0,
    val city: String = "",
    val state: String = "",
    val country: String = ""
) : Parcelable
