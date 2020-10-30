package com.ilatyphi95.farmersmarket.data.entities

data class AddItem(
    val itemId: String,
    val name: String,
    val price: String,
    val quantity: Int,
    val imageUrl: String,
    val date: Long
) {
}