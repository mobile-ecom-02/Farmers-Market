package com.ilatyphi95.farmersmarket.data.entities

import com.google.firebase.firestore.DocumentId

data class Category(@DocumentId val id: String = "", val type: String = ""){
    override fun toString(): String {
        return type
    }
}