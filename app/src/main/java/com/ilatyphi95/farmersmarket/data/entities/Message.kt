package com.ilatyphi95.farmersmarket.data.entities

import com.google.firebase.Timestamp

data class Message(
    val id: String,
    val message: String,
    val senderID: String,
    val imageUrl: String,
    val senderName: String,
    val timestamp: Timestamp,
) {
}