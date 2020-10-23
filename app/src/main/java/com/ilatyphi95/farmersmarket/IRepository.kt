package com.ilatyphi95.farmersmarket

import android.net.Uri
import androidx.lifecycle.LiveData
import com.ilatyphi95.farmersmarket.data.entities.CloseByProduct
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.entities.User

interface IRepository {
    fun searchProducts(searchString: String): LiveData<List<Product>>
    suspend fun getUser(sellerId: String): User
    suspend fun getRecentProducts(): List<Product>
    suspend fun getCloseByProduct(): List<CloseByProduct>
    suspend fun getCategory() : List<String>
    suspend fun uploadPicture(file: Uri?): String
    fun getCurrentUser(): User
    fun insertProduct(product: Product)
}
