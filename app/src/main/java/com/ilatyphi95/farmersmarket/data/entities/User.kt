package com.ilatyphi95.farmersmarket.data.entities


data class User (

    val id: String = "",

    val firstName: String,

    val lastName: String,

    val email: String,

    val phone: String,

    val profilePicUrl: String,

    val profileDisplayName: String,

    val location: String


)