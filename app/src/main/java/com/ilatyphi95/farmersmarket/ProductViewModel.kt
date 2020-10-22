package com.ilatyphi95.farmersmarket

import androidx.lifecycle.*
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.entities.User
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem
import com.ilatyphi95.farmersmarket.utils.Event
import com.ilatyphi95.farmersmarket.utils.ProductPicture
import com.ilatyphi95.farmersmarket.utils.ProductSmallBannerViewModel
import com.ilatyphi95.farmersmarket.utils.toRecyclerItem
import kotlinx.coroutines.*

class ProductViewModel(val product: Product, repository: IRepository) : ViewModel() {
    init {
        CoroutineScope(Job() + Dispatchers.Main).launch {
            withContext(Dispatchers.IO){
                _sellerDetails.postValue(repository.getUser(product.sellerId))
            }
        }
    }
    private val _sellerDetails = MutableLiveData<User>()

    val enablePhone = Transformations.map(_sellerDetails) {
        it != null && it.phone.isNotEmpty()
    }

    val similarItems: LiveData<List<RecyclerItem>> = Transformations
        .map(repository.searchProducts(product.name)) { list ->
            list.map { createProductSmallBannerViewModel(it) }
                .map { it.toRecyclerItem() }
        }

    val imgUrls: List<RecyclerItem> =
        product.imgUrls.map { ProductPicture(it) }.map { it.toRecyclerItem() }

    private val _pictureSelected = MutableLiveData<Int>()
    val pictureSelectedString : LiveData<String> = _pictureSelected.map {
        "${it +1}/${product.imgUrls.size}"
    }

    private val _eventProductSelected = MutableLiveData<Event<Product>>()
    val eventProductSelected : LiveData<Event<Product>>
        get() = _eventProductSelected

    fun callSeller() {

    }

    fun chatSeller() {

    }

    fun createProductSmallBannerViewModel(product: Product): ProductSmallBannerViewModel {
        return ProductSmallBannerViewModel(product).apply {
            itemClickHandler = { product -> productClicked(product) }
        }
    }

    private fun productClicked(product: Product) {
        _eventProductSelected.value = Event(product)
    }

    fun pictureSelected(position: Int) {
        _pictureSelected.value = position
    }
}

@Suppress("UNCHECKED_CAST")
class ProductViewModelFactory(private val product: Product, private val repository: IRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (ProductViewModel(product, repository) as T)
}
