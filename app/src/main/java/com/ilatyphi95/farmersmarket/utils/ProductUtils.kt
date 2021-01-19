package com.ilatyphi95.farmersmarket.utils

import com.ilatyphi95.farmersmarket.BR
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.data.entities.AdItem
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem

class ProductSmallBannerViewModel(val adItem: AdItem) {
    lateinit var itemClickHandler: (adItem: AdItem) -> Unit

    fun onItemClick() {
        itemClickHandler(adItem)
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

class CloseProductViewModel(val product: Product) {
    lateinit var itemClickHandler: (product: Product) -> Unit

    fun onItemClick() {
        itemClickHandler(product)
    }
}

fun CloseProductViewModel.toRecyclerItem() = RecyclerItem(
    data = this,
    layoutId = R.layout.close_product_item,
    variableId = BR.viewModel
)

sealed class PictureIconViewModel(val image: ProductImage?) {
    lateinit var removeItemHandler: (image: ProductImage?) -> Unit
    lateinit var addItemHandler: () -> Unit
    open fun addItem() { addItemHandler() }
    open fun removeItem() { removeItemHandler(image)}
}

fun PictureIconViewModel.toRecyclerItem() = RecyclerItem(
    data = this,
    layoutId = when(this) {
        is AddIcon -> R.layout.add_icon
        is AddedProductPicture -> R.layout.product_picture_layout },
    variableId = BR.viewModel
)

class AddIcon : PictureIconViewModel(null) {
    // do nothing
    override fun removeItem() {
    }
}

class AddedProductPicture(image: ProductImage) : PictureIconViewModel(image) {

    // do nothing
    override fun addItem() {
    }
}

