package com.ilatyphi95.farmersmarket.data.entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Message(
    @DocumentId val id: String = "",
    val counter: Int = 0,
    val message: String = "",
    val senderID: String = "",
    val imageUrl: String = "",
    val correspondentName: String = "",
    val timeStamp: Timestamp = Timestamp.now()
) {
}