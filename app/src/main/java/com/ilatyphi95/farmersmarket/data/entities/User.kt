package com.ilatyphi95.farmersmarket.data.entities

import com.google.firebase.firestore.DocumentId

/**
 * @param id unigue to each user
 * @param firstName must be provided
 * @param lastName must be provided
 * @param email must be provided
 * @param phone optional
 * @param profilePicUrl optional
 * @param profileDisplayName must be provided
 * @param location must be provided
 */
data class User @JvmOverloads constructor(
    @DocumentId var id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val profilePicUrl: String = "",
    val profileDisplayName: String = "",
    val location: MyLocation? = null
)
