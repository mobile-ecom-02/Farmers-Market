package com.ilatyphi95.farmersmarket.ui.modifyad

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import androidx.work.*
import com.ilatyphi95.farmersmarket.data.entities.Category
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

const val SEPARATOR = '-'

@ExperimentalCoroutinesApi
@Suppress("UnstableApiUsage")
class ModifyAdViewModel(application: Application, private val product: Product?, private val service: ProductServices) :
    AndroidViewModel(application) {

    private val _category = MutableLiveData<Category>()
    val category: LiveData<Category>
        get() = _category

    val title = MutableLiveData<String>()
    val firstName = MutableLiveData<String>()
    val lastName = MutableLiveData<String>()
    val price = MutableLiveData<String>()
    val currency = MutableLiveData<Currency>()
    val quantityAvailable = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val description = MutableLiveData<String>()

    private val _location = MutableLiveData<MyLocation>()
    val address: LiveData<String> = _location.map {
        "${it.city}, ${it.state}"
    }

    var loadedList: List<String> = emptyList()
    private lateinit var currencies: List<Currency>
    private lateinit var categories: List<Category>

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading


    private val _events = MutableLiveData<Event<Loads>>()
    val events: LiveData<Event<Loads>>
        get() = _events

    private val _eventNotification = MutableLiveData<Event<String>>()
    val eventNotification: LiveData<Event<String>>
        get() = _eventNotification


    private val _pictures = MutableLiveData<List<ProductImage>>(emptyList())
    val pictures: LiveData<List<RecyclerItem>> = _pictures.map { list ->

        preparePictureRecycler(
            list.filter{ it !is ImageDeleted }.map { productImage ->
                AddedProductPicture(productImage).apply {
                    removeItemHandler = { removePicture(productImage) }
                }.toRecyclerItem()
            })
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

        if (product != null) {
            initializeProduct(product)
        }
    }

    private fun initializeProduct(product: Product) {
        val currencyParts = product.priceStr.split(SEPARATOR)
        // set category
        viewModelScope.launch {
            _category.postValue(service.getCategories().find { it.id == product.type })
        }

        // set currency
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                currency.postValue(loadValidCurrencies().find { it.currencyCode == currencyParts[0] })
            }
        }

        title.postValue(product.name)
        description.postValue(product.description)
        price.postValue(currencyParts[1])
        description.postValue(product.description)
        quantityAvailable.postValue(product.qtyAvailable.toString())
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val imageList: List<ImageUploaded> = product.imgUrls.map { ImageUploaded(it) }
                _pictures.postValue(imageList)
            }
        }
    }

    private fun removePicture(image: ProductImage) {
        val oldList = _pictures.value.orEmpty().toMutableList()
        val position = oldList.indexOf(image)
        oldList.removeAt(position)

        if(image is ImageUploaded) {
            oldList.add(position, ImageDeleted(image.imageAddress))
        }
        _pictures.value = oldList

    }

    fun onClickCategory() {
        _isLoading.value = true
        viewModelScope.launch {
            categories = service.getCategories().sortedBy { it.type }
            loadedList = categories.map { it.type }
            _events.postValue(Event(Loads.LOAD_CATEGORY))
        }
    }

    fun onClickCurrency() {
        _isLoading.value = true
        viewModelScope.launch {
            withContext(Dispatchers.Default) {

                currencies = loadValidCurrencies()

                loadedList = currencies.map {
                    "${it.displayName} (${it.symbol})"
                }

                _events.postValue(Event(Loads.LOAD_CURRENCY))
            }
        }
    }

    private fun loadValidCurrencies(): List<Currency> {
        // remove old currencies
        val unneededCurrencyPattern = "[0-9]{4}(-[0-9]{4})?".toRegex()

        return Currency.getAvailableCurrencies()
            .filter { !it.displayName.contains(unneededCurrencyPattern) }
            .sortedBy { it.displayName }
    }

    private fun addPicture() {
        _events.value = Event(Loads.ADD_PICTURES)
    }

    fun addImages(imageList: List<Uri>) {
        val oldList = _pictures.value.orEmpty().toMutableList()
        oldList.addAll(imageList.map { ImageAdded(it) })
        _pictures.value = oldList
    }

    fun updateCurrency(position: Int) {
        currency.value = currencies[position]
    }

    fun selectCategory(position: Int) {
        _category.value = categories[position]
    }

    fun finishLoading() {
        _isLoading.postValue(false)
    }

    fun postAd() {
        _isLoading.value = true
        val list = _pictures.value!!

        val imageList: List<Uri> = (list.filterIsInstance<ImageAdded>()).map{it.imageAddress}

        val keywords = getKeywords(title = title.value!!, description = description.value!!)

        if(product == null) {

            // add new product
            val myProduct = Product(
                name = title.value!!,
                description = description.value!!,
                sellerId = service.getThisUserUid()!!,
                type = category.value!!.id,
                imgUrls = emptyList(),
                qtyAvailable = quantityAvailable.value!!.toInt(),
                qtySold = 0,
                priceStr = "${currency.value!!.currencyCode}$SEPARATOR${price.value!!}",
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

                uploadImage(imageList, uploadId)
            }
        } else {
            // update product

            val myProduct = product.copy(
                name = title.value!!,
                description = description.value!!,
                sellerId = service.getThisUserUid()!!,
                type = category.value!!.id,
                imgUrls = list.filterIsInstance<ImageUploaded>().map { it.imageAddress },
                qtyAvailable = quantityAvailable.value!!.toInt(),
                priceStr = "${currency.value!!.currencyCode}$SEPARATOR${price.value!!}",
                location = _location.value!!,
                keywords = keywords
            )

            viewModelScope.launch {
                val uploadId = service.upDateAd(myProduct)
                finishLoading()

                if (uploadId == null) {
                    // notify error
                    return@launch
                }

                postNotification("Ad Updated!")
                _events.postValue(Event(Loads.NAVIGATE_PRODUCT))

                val imagesNeededToBeRemoved = list.filterIsInstance<ImageDeleted>().map{it.imageAddress}
                service.removeImages(getApplication(), imagesNeededToBeRemoved)

                uploadImage(imageList, uploadId)
            }
        }
    }

    private fun uploadImage(list: List<Uri>, productId: String) {
        val uploadWorker = WorkManager.getInstance(getApplication())
        val myList = list.toMutableList()

        var continuation = uploadWorker.beginWith(
            OneTimeWorkRequestBuilder<UploadWorker>()
                .setConstraints(uploadConstraint())
                .setInputData(createInputDataForUri(myList.removeFirst().toString(), true, list.size))
                .build()
        )

        // queue up the image needed to be uploaded
        for (i in myList) {

            val uploadRequest = OneTimeWorkRequestBuilder<UploadWorker>()
                .setConstraints(uploadConstraint())
                .setInputData(createInputDataForUri(i.toString()))
                .build()
            continuation = continuation.then(uploadRequest)
        }


        val updateImageRequest = OneTimeWorkRequestBuilder<UpdateImageLinkWorker>()
            .setInputData(Data.Builder().putString(KEY_PRODUCT_ID, productId).build())
            .build()

        // add product image link update
        continuation = continuation.then(updateImageRequest)

        continuation.enqueue()
    }

    private fun uploadConstraint(): Constraints {
        return Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }

    private fun createInputDataForUri(imageUri: String, firstValue: Boolean = false, totalImage: Int = 1) : Data {
        val builder = Data.Builder()

        builder.putString(KEY_IMAGE_URL, imageUri)

        if(firstValue) {
            builder.putStringArray(KEY_OUTPUT_URL_LIST, emptyArray())
            builder.putInt(KEY_TOTAL_IMAGE, totalImage)
        }

        return builder.build()
    }

    private fun postNotification(message: String) {
        _eventNotification.postValue(Event(message))
    }

    private fun preparePictureRecycler(list: List<RecyclerItem>): List<RecyclerItem> {

        // the add icon is always the first item on the list
        val addIcon = AddIcon().apply {
            addItemHandler = { addPicture() }
        }.toRecyclerItem()

        val pictureList = mutableListOf(addIcon)
        pictureList.addAll(list)

        return pictureList
    }

    fun setCategory(category: Category) {
        _category.value = category
    }

    fun updateLocation(lastLocation: MyLocation) {
        _location.postValue(lastLocation)
    }
}

@ExperimentalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class AddProductViewModelFactory(
    private val application: Application,
    private val product: Product?,
    private val service: ProductServices
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (ModifyAdViewModel(application, product, service) as T)
}