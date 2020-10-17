package com.ilatyphi95.farmersmarket

import androidx.lifecycle.LiveData
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.entities.User

interface IRepository {
    fun searchProducts(searchString: String): LiveData<List<Product>>
    suspend fun getUser(sellerId: String): User

}
