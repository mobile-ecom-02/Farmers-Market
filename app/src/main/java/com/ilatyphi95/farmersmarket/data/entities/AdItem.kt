package com.ilatyphi95.farmersmarket.data.entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class AdItem(
    @DocumentId val itemId: String,
    val name: String,
    val price: String,
    val quantity: Int,
    val imageUrl: String,
    @ServerTimestamp val timestamp: Timestamp = Timestamp.now()
)