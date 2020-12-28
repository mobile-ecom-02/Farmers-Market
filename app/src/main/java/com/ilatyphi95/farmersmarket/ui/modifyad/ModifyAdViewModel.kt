package com.ilatyphi95.farmersmarket.ui.modifyad

import android.net.Uri
import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.ilatyphi95.farmersmarket.data.entities.MyLocation
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem
import com.ilatyphi95.farmersmarket.firebase.services.ProductServices
import com.ilatyphi95.farmersmarket.utils.*
import kotlinx.coroutines.*
import java.util.*


enum class Loads {
    ADD_PICTURES,
    LOAD_CURRENCY,
    LOAD_CATEGORY,
    NAVIGATE_PRODUCT
}

@ExperimentalCoroutinesApi
@Suppress("UnstableApiUsage")
class ModifyAdViewModel(private val service: ProductServices) : ViewModel() {

    private val _category = MutableLiveData<String>()
    val category: LiveData<String>
        get() = _category

    val title = MutableLiveData<String>()
    val firstName = MutableLiveData<String>()
    val lastName = MutableLiveData<String>()
    val price = MutableLiveData<String>()
    val currency = MutableLiveData<Currency>()
    val quantityAvailable = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val description = MutableLiveData<String>()

    private val imageUriList = mutableListOf<Uri>()
    private lateinit var documentAddress: DocumentReference
    private var pictureCount: Int = 0
    private var currentPictureUpload: Int = 0

    private val _location = MutableLiveData<MyLocation>()
    val address: LiveData<String> = _location.map {
        "${it.city}, ${it.state}"
    }

    var loadedList: List<String> = emptyList()
    private lateinit var currencies: List<Currency>

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading


    private val _events = MutableLiveData<Event<Loads>>()
    val events: LiveData<Event<Loads>>
        get() = _events

    private val _eventNotification = MutableLiveData<Event<String>>()
    val eventNotification: LiveData<Event<String>>
        get() = _eventNotification


    private val _pictures = MutableLiveData<List<Uri?>>(listOf(null))
    val pictures: LiveData<List<RecyclerItem>> = _pictures.map { list ->
        list.map { pictureUri ->
            val item: RecyclerItem?

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

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                service.getUser(service.getThisUserUid()!!)?.let { thisUser ->
                    if (thisUser.firstName.isNotBlank()) firstName.postValue(thisUser.firstName)
                    if (thisUser.lastName.isNotBlank()) lastName.postValue(thisUser.lastName)
                }
            }
        }
    }

    private fun removePicture(imageUri: Uri?) {
        val oldList = _pictures.value.orEmpty().toMutableList()
        oldList.remove(imageUri)
        _pictures.value = oldList

    }

    fun onClickCategory() {
        _isLoading.value = true
        viewModelScope.launch {
            loadedList = service.getCategories().map { it.type }
            _events.postValue(Event(Loads.LOAD_CATEGORY))
        }
    }

    fun onClickCurrency() {
        _isLoading.value = true
        viewModelScope.launch {
            withContext(Dispatchers.Default) {

                // remove old currencies
                val unneededCurrencyPattern = "[0-9]{4}(-[0-9]{4})?".toRegex()
                currencies = Currency.getAvailableCurrencies()
                    .filter { !it.displayName.contains(unneededCurrencyPattern) }
                    .sortedBy { it.displayName }

                loadedList = currencies.map {
                    "${it.displayName} (${it.symbol})"
                }

                _events.postValue(Event(Loads.LOAD_CURRENCY))
            }
        }
    }

    private fun addPicture() {
        _events.value = Event(Loads.ADD_PICTURES)
    }

    fun addImages(imageList: List<Uri>) {
        val oldList = _pictures.value.orEmpty().toMutableList()
        oldList.addAll(imageList)
        _pictures.value = oldList
    }

    fun updateCurrency(position: Int) {
        currency.value = currencies[position]
    }

    fun selectCategory(position: Int) {
        _category.value = loadedList[position]
    }

    fun finishLoading() {
        _isLoading.postValue(false)
    }

    fun postAd() {
        _isLoading.value = true
        val list = _pictures.value!!

        // keep the list of files that will be uploaded
        imageUriList.clear()

        imageUriList.addAll(list.subList(1, list.size).filterNotNull())
        pictureCount = imageUriList.size

        val keywords = getKeywords(title = title.value!!, description = description.value!!)

        val myProduct = Product(
            name = title.value!!,
            description = description.value!!,
            sellerId = service.getThisUserUid()!!,
            type = category.value!!,
            imgUrls = emptyList(),
            qtyAvailable = quantityAvailable.value!!.toInt(),
            qtySold = 0,
            priceStr = "${currency.value!!.currencyCode}-${price.value!!}",
            location = _location.value!!,
            keywords = keywords
        )

        viewModelScope.launch {
            val uploadId = service.uploadAd(myProduct)
            finishLoading()

            if (uploadId == null) {
                // notify error
                return@launch
            }

            postNotification("Ad posted!")
            _events.postValue(Event(Loads.NAVIGATE_PRODUCT))
            documentAddress = FirebaseFirestore.getInstance().collection("ads").document(uploadId)
            uploadImageToFirebaseStorage()
        }
    }

    private fun postNotification(message: String) {
        _eventNotification.postValue(Event(message))
    }

    private fun uploadImageToFirebaseStorage() {
        if (imageUriList.size > 0) {
            val imageUri = imageUriList[0]

            imageUriList.removeAt(0)
            currentPictureUpload += 1
            viewModelScope.launch {
                service.uploadImage(imageUri)?.let {
                    documentAddress.update("imgUrls", FieldValue.arrayUnion(it))
                    postNotification("Image $currentPictureUpload/$pictureCount upload Successful!")

                    // recursive call
                    uploadImageToFirebaseStorage()
                }
            }
        }
    }

    fun setCategory(string: String) {
        _category.value = string
    }

    fun updateLocation(lastLocation: MyLocation) {
        _location.postValue(lastLocation)
    }
}

@ExperimentalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class AddProductViewModelFactory(private val service: ProductServices) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (ModifyAdViewModel(service) as T)
}