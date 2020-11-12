package com.ilatyphi95.farmersmarket.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import com.ilatyphi95.farmersmarket.data.entities.AdItem
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.repository.IRepository
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem
import com.ilatyphi95.farmersmarket.firebase.addToRecent
import com.ilatyphi95.farmersmarket.utils.*
import kotlinx.coroutines.*

class HomeViewModel(private val repository: IRepository) : ViewModel() {

    private val uiScope = Job() + Dispatchers.Main

    private val _recentItems = MutableLiveData<List<AdItem>>()

    val recentItems = _recentItems.map { list ->

        list.map {
            ProductSmallBannerViewModel(it).apply {
                itemClickHandler = { it -> adItemSelected(it) }
            }
        }.map { it.toRecyclerItem() }
    }


    private val _closeBy = MutableLiveData<List<RecyclerItem>>()
    val closeBy: LiveData<List<RecyclerItem>>
        get() = _closeBy

    private val searchString = MutableLiveData<String>()
    val showSearchRecycler = searchString.map { it != null && !it.isNullOrBlank() }
    val showRecentScreen = showSearchRecycler.map { it.not() }

    val searchProduct = searchString.switchMap { srcStr ->
        val mySearch = searchTransform(srcStr)
        _eventIsLoading.value = false
        mySearch
    }

    private val _eventProductSelected = MutableLiveData<Event<Product>>()
    val eventProductSelected: LiveData<Event<Product>>
        get() = _eventProductSelected

    private val _eventIsLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _eventIsLoading


    init {
        CoroutineScope(uiScope).launch {
            withContext(Dispatchers.IO) {

                _closeBy.postValue(repository.getCloseByProduct()
                    .map {
                        CloseProductViewModel(it).apply {
                            itemClickHander = { it -> productSelected(it.product) }
                        }
                    }.map { it.toRecyclerItem() })
            }
        }
        clearSearch()
    }

    private fun productSelected(product: Product) {
        addToRecent(product)
        _eventProductSelected.postValue(Event(product))
    }

    private fun adItemSelected(adItem: AdItem) {
        _eventIsLoading.postValue(true)
        FirebaseFirestore.getInstance().collection("ads")
            .whereEqualTo(FieldPath.documentId(), adItem.itemId).limit(1).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val products = it.result.toObjects<Product>()
                    if (products.isNotEmpty()) {
                        _eventProductSelected.postValue(Event(products[0]))
                    } else {
                        // handle the case of zero elements
                        Log.d("Home ViewModel", "adItemSelected: Zero elements")
                    }
                } else {
                    Log.d("Home ViewModel", "adItemSelected: ${it.exception?.message}")
                }
            }
    }

    override fun onCleared() {
        uiScope.cancel()
        super.onCleared()
    }

    fun search(query: String?) {
        searchString.value = query
    }

    private fun clearSearch() {
        searchString.value = null
    }

    private fun searchTransform(searchStr: String?): LiveData<List<RecyclerItem>> {
        _eventIsLoading.value = true

        return if (searchStr != null) {
            repository.searchProducts(searchStr).map { list ->
                list.map { product ->
                    SearchProductViewModel(product).apply {
                        itemClickHandler = { productSelected(product) }
                    }
                }.map { it.toRecyclerItem() }

            }
        } else {
            liveData { emit(emptyList<RecyclerItem>()) }
        }
    }

    fun updateRecent(list: List<AdItem>) {
        _recentItems.postValue(list)
    }
}

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(private val repository: IRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (HomeViewModel(repository) as T)
}