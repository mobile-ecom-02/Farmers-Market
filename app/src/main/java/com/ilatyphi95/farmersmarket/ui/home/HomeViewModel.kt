package com.ilatyphi95.farmersmarket.ui.home

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.ilatyphi95.farmersmarket.data.entities.AdItem
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem
import com.ilatyphi95.farmersmarket.firebase.addToRecent
import com.ilatyphi95.farmersmarket.firebase.services.ProductServices
import com.ilatyphi95.farmersmarket.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ExperimentalCoroutinesApi
class HomeViewModel(private val service: ProductServices) : ViewModel() {

    private val _recentItems = service.recentItems().asLiveData()

    private val _showSearchResult = MutableLiveData(false)

    val recentItems = _recentItems.map { list ->

        list.map {
            ProductSmallBannerViewModel(it).apply {
                itemClickHandler = { it -> adItemSelected(it) }
            }
        }.map { it.toRecyclerItem() }
    }

    private val _closeBy = MutableLiveData<List<Product>>()
    val closeBy: LiveData<List<RecyclerItem>> = _closeBy.map { list ->
            list.map {
                CloseProductViewModel(it).apply {
                    itemClickHandler = { it -> productSelected(it) }
                }
            }.map { it.toRecyclerItem() }
    }

    val showSearchRecycler = _showSearchResult.map { it }
    val showRecentScreen = showSearchRecycler.map { it.not() }

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

    private fun updateCloseBy(list: List<Product>) {

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val closeByList = list.filter { it.sellerId != service.getThisUserUid() }
                _closeBy.postValue(closeByList)
            }
        }
    }

    fun closeSearchView() {
        _showSearchResult.value = false
    }

    fun openSearchView() {
        _showSearchResult.value = true
    }

    fun productFlow(query: String): Flow<PagingData<RecyclerItem>> {
        return service.productPager(searchTerm(query))
            .map { pagingProductList ->
                pagingProductList.map {
                    SearchProductViewModel(it).apply {
                        itemClickHandler = { productSelected(product) }
                    }.toRecyclerItem()
                }
            }
            .cachedIn(viewModelScope)
    }
}

@ExperimentalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(private val service: ProductServices) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (HomeViewModel(service) as T)
}