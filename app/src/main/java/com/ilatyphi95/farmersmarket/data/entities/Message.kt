package com.ilatyphi95.farmersmarket.data.entities

data class Message(
    val id: String,
    val message: String,
    val senderID: String,
    val imageUrl: String,
    val senderName: String,
    val timestamp: Long,
) {
}