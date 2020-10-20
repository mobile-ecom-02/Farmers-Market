package com.ilatyphi95.farmersmarket

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem
import com.ilatyphi95.farmersmarket.utils.AddIcon
import com.ilatyphi95.farmersmarket.utils.AddedProductPicture
import com.ilatyphi95.farmersmarket.utils.toRecyclerItem

class AddProductViewModel() : ViewModel() {
    private val _pictures = MutableLiveData(listOf(""))
    val pictures: LiveData<List<RecyclerItem>> = _pictures.map {list ->
        list.map {
            var item: RecyclerItem? = null

            if (it == "") {
                item = AddIcon().apply {
                    addItemHandler = { addPicture() }
                }.toRecyclerItem()
            } else {
                item = AddedProductPicture(it).apply {
                    removeItemHandler = { removePicture(it) }
                }.toRecyclerItem()
            }
            item
        }
    }

    private fun removePicture(it: String) {
        TODO("Not yet implemented")
    }

    private fun addPicture() {
        TODO("Not yet implemented")
    }
}