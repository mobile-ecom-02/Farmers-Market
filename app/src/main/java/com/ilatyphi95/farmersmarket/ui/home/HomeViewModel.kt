package com.ilatyphi95.farmersmarket.ui.home

import androidx.lifecycle.*
import com.ilatyphi95.farmersmarket.data.repository.IRepository
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem
import com.ilatyphi95.farmersmarket.utils.*
import kotlinx.coroutines.*

class HomeViewModel(private val repository: IRepository) : ViewModel() {

    private val uiScope = Job() + Dispatchers.Main

    private val _recentItems = MutableLiveData<List<RecyclerItem>>()
    val recentItems : LiveData<List<RecyclerItem>>
        get() = _recentItems

    private val _closeBy = MutableLiveData<List<RecyclerItem>>()
    val closeBy : LiveData<List<RecyclerItem>>
        get() = _closeBy

    private val searchString = MutableLiveData<String>()
    val showSearchRecycler = searchString.map { it != null && !it.isNullOrBlank() }
    val showRecentScreen = showSearchRecycler.map { it.not() }

    val searchProduct  = searchString.switchMap { srcStr ->
        val mySearch = searchTransform(srcStr)
        _eventIsLoading.value = false
        mySearch
    }

    private val _eventProductSelected = MutableLiveData<Event<Product>>()
    val eventProductSelected : LiveData<Event<Product>>
        get() = _eventProductSelected

    private val _eventIsLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean>
        get() = _eventIsLoading



    init {
        CoroutineScope(uiScope).launch {
            withContext(Dispatchers.IO) {

                _closeBy.postValue(repository.getCloseByProduct()
                    .map {
                    CloseProductViewModel(it).apply {
                        itemClickHander = {it -> productSelected(it.product)}
                    } }.map { it.toRecyclerItem() })


                _recentItems.postValue(repository.getRecentProducts()
                    .map {
                    ProductSmallBannerViewModel(it).apply {
                        itemClickHandler = {it -> productSelected(it)}
                    } }.map { it.toRecyclerItem() })
            }
        }
        clearSearch()
    }

    private fun productSelected(product: Product) {
        _eventProductSelected.postValue(Event(product))
    }

    override fun onCleared() {
        uiScope.cancel()
        super.onCleared()
    }

    fun search(query: String?) {
        searchString.value = query
    }

    fun clearSearch() {
        searchString.value = null
    }

    private fun searchTransform(searchStr: String?) : LiveData<List<RecyclerItem>> {
        _eventIsLoading.value = true

        return if(searchStr != null) {
            repository.searchProducts(searchStr).map{list->
                list.map {product -> SearchProductViewModel(product).apply {
                    itemClickHandler = {productSelected(product)}
                } }.map { it.toRecyclerItem() }

            }}
        else {
            liveData {  emit(emptyList<RecyclerItem>()) }
        }
    }
}


@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(private val repository: IRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (HomeViewModel(repository) as T)
}