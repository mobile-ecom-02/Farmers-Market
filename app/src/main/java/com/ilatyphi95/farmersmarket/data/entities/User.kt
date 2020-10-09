package com.ilatyphi95.farmersmarket.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "first_name")
    val firstName: String?,

    @ColumnInfo(name = "last_name")
    val lastName: String,

    @ColumnInfo(name = "email")
    val email: String?,

    @ColumnInfo(name = "phone")
    val phone: String?,

    @ColumnInfo(name = "profile_pic_url")
    val profilePicUrl: String?,

    @ColumnInfo(name = "location")
    val location: String?


)