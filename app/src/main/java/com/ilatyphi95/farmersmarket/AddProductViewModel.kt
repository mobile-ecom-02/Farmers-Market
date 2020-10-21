package com.ilatyphi95.farmersmarket

import android.net.Uri
import androidx.lifecycle.*
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem
import com.ilatyphi95.farmersmarket.utils.*

const val ADD_PICTURES = 100
class AddProductViewModel(repository: IRepository) : ViewModel() {
    val title = MutableLiveData<String>()
    val firstName = MutableLiveData<String>()
    val lastName = MutableLiveData<String>()
    val price = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val description = MutableLiveData<String>()

    private val _events = MutableLiveData<Event<Int>>()
    val events : LiveData<Event<Int>>
        get() = _events


    private val _pictures = MutableLiveData<List<Uri?>>(listOf(null))
    val pictures: LiveData<List<RecyclerItem>> = _pictures.map {list ->
        list.map { pictureUri ->
            var item: RecyclerItem? = null

            if (pictureUri == null) {
                item = AddIcon().apply {
                    addItemHandler = { addPicture() }
                }.toRecyclerItem()
            } else {
                item = AddedProductPicture(pictureUri).apply {
                    removeItemHandler = { removePicture(pictureUri) }
                }.toRecyclerItem()
            }
            item
        }
    }

    private fun removePicture(imageUri: Uri?) {
        val oldList = _pictures.value.orEmpty().toMutableList()
        oldList.remove(imageUri)
        _pictures.value = oldList

    }

    private fun addPicture() {
        _events.value = Event(ADD_PICTURES)
    }

    fun addImages(imageList: List<Uri>) {
        val oldList = _pictures.value.orEmpty().toMutableList()
        oldList.addAll(imageList)
        _pictures.value = oldList
    }
}

@Suppress("UNCHECKED_CAST")
class AddProductViewModelFactory(private val repository: IRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (AddProductViewModel(repository) as T)
}