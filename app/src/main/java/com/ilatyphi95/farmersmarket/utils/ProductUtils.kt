package com.ilatyphi95.farmersmarket.utils

import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.BR
import com.ilatyphi95.farmersmarket.data.entities.CloseByProduct
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
    layoutId = R.layout.small_product_item,
    variableId = BR.viewModel
)

class SearchProductViewModel(val product: Product) {
    lateinit var itemClickHandler: (product: Product) -> Unit

    fun onItemClick() {
        itemClickHandler(product)
    }
}

fun SearchProductViewModel.toRecyclerItem() = RecyclerItem(
    data = this,
    layoutId = R.layout.search_product_item,
    variableId = BR.viewModel
)

class ProductPicture(val imageUrl: String) {
    lateinit var itemClickHandler: (imageUrl: String) -> Unit

    fun onItemClick() {
        itemClickHandler(imageUrl)
    }
}

fun ProductPicture.toRecyclerItem() = RecyclerItem(
    data = this,
    layoutId = R.layout.picture_item,
    variableId = BR.viewModel
)

class CloseProductViewModel(val closeByProduct: CloseByProduct) {
    lateinit var itemClickHander: (closeByProduct: CloseByProduct) -> Unit

    fun onItemClick() {
        itemClickHander(closeByProduct)
    }
}

fun CloseProductViewModel.toRecyclerItem() = RecyclerItem(
    data = this,
    layoutId = R.layout.close_product_item,
    variableId = BR.viewModel
)
