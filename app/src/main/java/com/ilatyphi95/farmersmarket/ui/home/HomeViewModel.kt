package com.ilatyphi95.farmersmarket.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import com.ilatyphi95.farmersmarket.data.entities.AdItem
import com.ilatyphi95.farmersmarket.data.entities.CloseByProduct
import com.ilatyphi95.farmersmarket.data.entities.MyLocation
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.repository.IRepository
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem
import com.ilatyphi95.farmersmarket.firebase.addToRecent
import com.ilatyphi95.farmersmarket.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class HomeViewModel(private val repository: IRepository) : ViewModel() {

    private val uiScope = Job() + Dispatchers.Main

    private val _recentItems = MutableLiveData<List<AdItem>>()

    private val _showSearchResult = MutableLiveData(false)

    private val user = FirebaseAuth.getInstance().currentUser

    val recentItems = _recentItems.map { list ->

        list.map {
            ProductSmallBannerViewModel(it).apply {
                itemClickHandler = { it -> adItemSelected(it) }
            }
        }.map { it.toRecyclerItem() }
    }

    private val _closeBy = MutableLiveData<List<CloseByProduct>>()
    val closeBy: LiveData<List<RecyclerItem>> = _closeBy.map { list ->
            list.map {
                CloseProductViewModel(it).apply {
                    itemClickHander = { it -> productSelected(it.product) }
                }
            }.map { it.toRecyclerItem() }
    }

    val showSearchRecycler = _showSearchResult.map { it }
    val showRecentScreen = showSearchRecycler.map { it.not() }

    private val _searchProduct = MutableLiveData<List<RecyclerItem>>()
    val searchProduct : LiveData<List<RecyclerItem>>
        get() = _searchProduct

    private val _eventProductSelected = MutableLiveData<Event<Product>>()
    val eventProductSelected: LiveData<Event<Product>>
        get() = _eventProductSelected

    private val _eventIsLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _eventIsLoading

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
        query?.let {
            loadSearch(query)
        }
    }

    private fun loadSearch(query: String) {
        val search = searchTerm(query)

        viewModelScope.launch {
            val queryObjects = FirebaseFirestore.getInstance().collection("ads")
                .whereNotEqualTo("sellerId", user!!.uid)
                .whereArrayContains("keywords", search)
                .get().await()

            val list: List<RecyclerItem> = queryObjects.toObjects(Product::class.java).map {
                SearchProductViewModel(it).apply {
                    itemClickHandler = { productSelected(product) }
                }.toRecyclerItem()
            }

            _searchProduct.value = list
        }

    }

    fun updateRecent(list: List<AdItem>) {
        _recentItems.postValue(list)
    }

    fun updateCloseBy(myLocation: MyLocation, list: List<Product>) {
        CoroutineScope(uiScope).launch {
            withContext(Dispatchers.Default) {
                val closeByList = list.filter { it.sellerId != user!!.uid }.map {
                    CloseByProduct(it, myLocation.toLocation().distanceTo(it.location?.toLocation()))
                }
                _closeBy.postValue(closeByList.sortedBy { it.distance })
            }
        }
    }

    fun closeSearchView() {
        _showSearchResult.value = false
    }

    fun openSearchView() {
        _showSearchResult.value = true
    }
}

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(private val repository: IRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (HomeViewModel(repository) as T)
}