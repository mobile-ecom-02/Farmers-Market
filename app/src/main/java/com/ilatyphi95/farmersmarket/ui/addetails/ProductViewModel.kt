package com.ilatyphi95.farmersmarket.ui.addetails

import androidx.lifecycle.*
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.entities.User
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem
import com.ilatyphi95.farmersmarket.firebase.addToInterested
import com.ilatyphi95.farmersmarket.firebase.addToRecent
import com.ilatyphi95.farmersmarket.firebase.services.ProductServices
import com.ilatyphi95.farmersmarket.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ProductViewModel(val product: Product, private val services: ProductServices) : ViewModel() {

    private val _eventMessage = MutableLiveData<Event<String>>()
    val eventMessage : LiveData<Event<String>>
        get() = _eventMessage

    private val _eventCall = MutableLiveData<Event<String>>()
    val eventCall : LiveData<Event<String>>
        get() = _eventCall

    private val _sellerDetails = MutableLiveData<User>()

    val enablePhone = _sellerDetails.map { it != null && it.phone.isNotEmpty() }

    private val _similarItems = MutableLiveData<List<Product>>()
    val similarItems: LiveData<List<RecyclerItem>> = _similarItems.map {  list ->
            list.map { createProductSmallBannerViewModel(it) }
                .map { it.toRecyclerItem() }
        }

    val isSimilarItemVisible: LiveData<Boolean> = similarItems.map { !it.isNullOrEmpty() }

    val imgUrls: List<RecyclerItem> =
        product.imgUrls.map { ProductPicture(it) }.map { it.toRecyclerItem() }

    private val _pictureSelected = MutableLiveData<Int>()
    val pictureSelectedString : LiveData<String> = _pictureSelected.map {
        "${it +1}/${product.imgUrls.size}"
    }

    private val _eventProductSelected = MutableLiveData<Event<Product>>()
    val eventProductSelected : LiveData<Event<Product>>
        get() = _eventProductSelected

    init {
        val searchTerm = searchTerm(product.name, product.description, limit = KEYWORD_LIMIT - 1)

        viewModelScope.launch {
            _similarItems.postValue(services.searchProduct(searchTerm).filter { it != product })
            _sellerDetails.postValue(services.getUser(product.sellerId))
        }
    }

    fun callSeller() {
        _sellerDetails.value?.let {
            if(it.phone.isNotEmpty()) {
                _eventCall.postValue(Event(it.phone))
                addToInterested(product)
            }
        }
    }

    fun chatSeller() {
        viewModelScope.launch {
            services.createMessage(product)?.let {
                _eventMessage.postValue(Event(it))
                addToInterested(product)
            }
        }
    }

    private fun createProductSmallBannerViewModel(product: Product): ProductSmallBannerViewModel {
        return ProductSmallBannerViewModel(product.toAdItem()).apply {
            itemClickHandler = { productClicked(product) }
        }
    }

    private fun productClicked(product: Product) {
        addToRecent(product)
        _eventProductSelected.value = Event(product)
    }

    fun pictureSelected(position: Int) {
        _pictureSelected.value = position
    }
}

@ExperimentalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class ProductViewModelFactory(private val product: Product, private val services: ProductServices) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (ProductViewModel(product, services) as T)
}
