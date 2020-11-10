package com.ilatyphi95.farmersmarket.data.entities

import com.google.firebase.Timestamp

data class AdItem(
    val itemId: String,
    val name: String,
    val price: String,
    val quantity: Int,
    val imageUrl: String,
    val timestamp: Timestamp = Timestamp.now()
)