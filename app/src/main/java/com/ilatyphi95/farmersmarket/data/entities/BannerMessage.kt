package com.ilatyphi95.farmersmarket.data.entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class BannerMessage(
    @DocumentId val id: String = "",
    val message: String = "",
    val senderID: String = "",
    val imageUrl: String = "",
    val senderName: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val counter: Int = 0
) {
}