package com.ilatyphi95.farmersmarket

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem
import com.ilatyphi95.farmersmarket.utils.ProductSmallBannerViewModel
import com.ilatyphi95.farmersmarket.utils.toRecyclerItem

class ProductViewModel(val product: Product, repository: IRepository) : ViewModel() {
    val similarItems: LiveData<List<RecyclerItem>> = Transformations
        .map(repository.searchProducts(product.name)) { list ->
            list.map { createProductSmallBannerViewModel(it) }
                .map { it.toRecyclerItem() }
        }

    fun callSeller() {

    }

    fun chatSeller() {

    }

    fun createProductSmallBannerViewModel(product: Product): ProductSmallBannerViewModel {
        return ProductSmallBannerViewModel(product).apply {
            itemClickHandler = { product -> productClicked(product) }
        }
    }

    fun productClicked(product: Product) {

    }

}