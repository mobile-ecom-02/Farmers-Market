package com.ilatyphi95.farmersmarket.ui.addetails

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ilatyphi95.farmersmarket.data.entities.MessageShell
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.entities.User
import com.ilatyphi95.farmersmarket.data.repository.IRepository
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem
import com.ilatyphi95.farmersmarket.firebase.addToInterested
import com.ilatyphi95.farmersmarket.firebase.addToRecent
import com.ilatyphi95.farmersmarket.utils.*
import kotlinx.coroutines.*

class ProductViewModel(val product: Product, repository: IRepository) : ViewModel() {

    private val _eventMessage = MutableLiveData<Event<String>>()
    val eventMessage : LiveData<Event<String>>
        get() = _eventMessage

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
        addToInterested(product)

    }

    fun chatSeller() {
        val sellerId = _sellerDetails.value?.id
        val buyerId = FirebaseAuth.getInstance().currentUser?.uid

        if(sellerId.isNullOrEmpty() || buyerId.isNullOrEmpty()) {
            // provide notification
            return
        }
        val messageId = product.id.substring(0,10) + sellerId.substring(0, 10) + buyerId.substring(0, 10)

        val messageShell = MessageShell(
            productId = product.id,
            imgUrl = product.imgUrls.getOrElse(0){""},
            participants = listOf(sellerId, buyerId))

        FirebaseFirestore.getInstance().document("messages/${messageId}")
            .set(messageShell, SetOptions.merge()).addOnSuccessListener {
                _eventMessage.postValue(Event(messageId))
            }
        addToInterested(product)
    }

    fun createProductSmallBannerViewModel(product: Product): ProductSmallBannerViewModel {
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

@Suppress("UNCHECKED_CAST")
class ProductViewModelFactory(private val product: Product, private val repository: IRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (ProductViewModel(product, repository) as T)
}
