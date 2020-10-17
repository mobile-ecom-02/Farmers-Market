package com.ilatyphi95.farmersmarket.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilatyphi95.farmersmarket.FarmersMarketApplication
import com.ilatyphi95.farmersmarket.data.universaladapter.DataBindingRecyclerAdapter
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem

@BindingAdapter("app:availableText")
fun TextView.availableText(qty: Int) {
    text = this.context.getString(R.string.availableText, qty)
}

@BindingAdapter("items")
fun setRecyclerViewItems(
    recyclerView: RecyclerView,
    items: List<RecyclerItem>?
) {
    var adapter = (recyclerView.adapter as? DataBindingRecyclerAdapter)
    if (adapter == null) {
        adapter = DataBindingRecyclerAdapter()
        recyclerView.adapter = adapter
    }

    adapter.submitList(
        items.orEmpty()
    )
}

@BindingAdapter("loadImage")
fun ImageView.loadImage(imageUrl: String?) {
    if (imageUrl != null) {
        this.context?.let {
            Glide.with(it)
                .load(imageUrl)
                .apply(FarmersMarketApplication.requestOption)
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
                .into(this)
        }
    }

}

@BindingAdapter("loadFirst")
fun ImageView.loadFirstImage(imageUrls: List<String>) {
    val firstValue = imageUrls.getOrElse(0){null}
    loadImage(firstValue)
}