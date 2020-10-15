package com.ilatyphi95.farmersmarket.utils

import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.BR
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem

class ProductSmallBannerViewModel(val product: Product) {
    lateinit var itemClickHandler: (product: Product) -> Unit

    fun onItemClick() {
        itemClickHandler(product)
    }
}

fun ProductSmallBannerViewModel.toRecyclerItem() = RecyclerItem(
    data = this,
    layoutId = R.layout.similar_item_banner,
    variableId = BR.product
)
