package com.ilatyphi95.farmersmarket.ui.home

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.ilatyphi95.farmersmarket.data.entities.AdItem
import com.ilatyphi95.farmersmarket.data.entities.CloseByProduct
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem
import com.ilatyphi95.farmersmarket.firebase.addToRecent
import com.ilatyphi95.farmersmarket.firebase.services.ProductServices
import com.ilatyphi95.farmersmarket.utils.*
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
class HomeViewModel(private val service: ProductServices) : ViewModel() {

    private val _recentItems = service.recentItems().asLiveData()

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

    init {
        viewModelScope.launch {
            updateCloseBy(service.closeByItems())
        }
    }

    private fun productSelected(product: Product) {
        addToRecent(product)
        _eventProductSelected.postValue(Event(product))
    }

    private fun adItemSelected(adItem: AdItem) {
        _eventIsLoading.postValue(true)
        viewModelScope.launch {
            service.getProduct(adItem.itemId)?.let {
                _eventProductSelected.postValue(Event(it))
            }
        }
    }

    fun search(query: String?) {
        query?.let {
            loadSearch(query)
        }
    }

    private fun loadSearch(query: String) {
        val search = searchTerm(query)

        viewModelScope.launch {
            _searchProduct.value = service.searchProduct(search).map {
                SearchProductViewModel(it).apply {
                    itemClickHandler = { productSelected(product) }
                }.toRecyclerItem()
            }
        }

    }

    private fun updateCloseBy(list: List<Product>) {

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val closeByList = list.filter { it.sellerId != user!!.uid }.map {
                    CloseByProduct(it, 100.0f)
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

@ExperimentalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(private val service: ProductServices) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (HomeViewModel(service) as T)
}