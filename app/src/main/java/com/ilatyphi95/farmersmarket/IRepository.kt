package com.ilatyphi95.farmersmarket

import androidx.lifecycle.LiveData
import com.ilatyphi95.farmersmarket.data.entities.Product

interface IRepository {
    abstract fun searchProducts(searchString: String): LiveData<List<Product>>

}
