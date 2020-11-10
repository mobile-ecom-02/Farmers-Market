package com.ilatyphi95.farmersmarket.utils

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.ilatyphi95.farmersmarket.FarmersMarketApplication
import com.ilatyphi95.farmersmarket.data.universaladapter.DataBindingRecyclerAdapter
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.data.entities.CloseByProduct
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem
import org.joda.money.Money

const val METERS_TO_MILES = 0.000621371

@BindingAdapter("app:availableText")
fun TextView.availableText(qty: Int) {
    text = this.context.getString(R.string.availableText, qty)
}

@BindingAdapter("app:distanceMiles")
fun TextView.distanceMiles(closeByProduct: CloseByProduct) {
    text = this.context.getString(R.string.distanceMiles,
        (closeByProduct.distance * METERS_TO_MILES).toInt())
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
@BindingAdapter("loadImage")
fun ImageView.loadImage(imageUri: Uri?) {

    if (imageUri != null) {
        this.context?.let {
            Glide.with(it)
                .load(BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri)))
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

@BindingAdapter("viewedAt")
fun TextView.viewedOn(timeStamp: Timestamp) {
    text = context.getString(R.string.viewed_at, toDate(context, timeStamp))
}

@BindingAdapter("postedAt")
fun TextView.postedOn(timeStamp: Timestamp) {
    text = context.getString(R.string.posted_at, toDate(context, timeStamp))
}

@BindingAdapter("time")
fun TextView.time(timeStamp: Timestamp) {
    text = toDate(context, timeStamp)
}

@BindingAdapter("moneyString")
fun TextView.moneyString(money: Money) {
    text = money.toString()
}
