package com.ilatyphi95.farmersmarket.ui.modifyad

import android.net.Uri
import androidx.lifecycle.*
import com.google.common.io.Files.getFileExtension
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ilatyphi95.farmersmarket.data.entities.MyLocation
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.entities.User
import com.ilatyphi95.farmersmarket.data.repository.IRepository
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem
import com.ilatyphi95.farmersmarket.utils.AddIcon
import com.ilatyphi95.farmersmarket.utils.AddedProductPicture
import com.ilatyphi95.farmersmarket.utils.Event
import com.ilatyphi95.farmersmarket.utils.toRecyclerItem
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.HashMap


enum class Loads {
    ADD_PICTURES,
    LOAD_CURRENCY,
    LOAD_CATEGORY,
    NAVIGATE_PRODUCT
}

@Suppress("UnstableApiUsage")
class ModifyAdViewModel(private val repository: IRepository) : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(job + Dispatchers.Main)

    var user: User = repository.getCurrentUser()
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
    private val firebaseStorageRef = FirebaseStorage.getInstance().reference.child("images")
    lateinit var documentAddress: DocumentReference
    var pictureCount : Int = 0
    var currentPictureUpload: Int = 0

    private val _location = MutableLiveData<MyLocation>()
    val address: LiveData<String> = _location.map {
        "${it.city}, ${it.state}"
    }

    var loadedList: List<String>? = emptyList()
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
        uiScope.launch {
            withContext(Dispatchers.IO) {
                //TODO: Add the id of the current user
                val user = repository.getUser("")

                if (user.firstName.isNotBlank()) firstName.postValue(user.firstName)
                if (user.lastName.isNotBlank()) lastName.postValue(user.lastName)
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
        uiScope.launch {
            withContext(Dispatchers.IO) {
                loadedList = repository.getCategory()
                _events.postValue(Event(Loads.LOAD_CATEGORY))
            }
        }
    }

    fun onClickCurrency() {
        _isLoading.value = true
        uiScope.launch {
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
        loadedList?.let {
            _category.value = it[position]
        }
    }

    fun finishLoading() {
        _isLoading.postValue(false)
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    fun postAd() {
        _isLoading.value = true
        val list = _pictures.value!!

        // keep the list of files that will be uploaded
        imageUriList.clear()

        imageUriList.addAll(list.subList(1, list.size).filterNotNull())
        pictureCount = imageUriList.size
        val user = FirebaseAuth.getInstance().currentUser

        val product = HashMap<String, Any>()
        product["name"] = title.value!!
        product["description"] = description.value!!
        product["sellerId"] = user!!.uid
        product["type"] = category.value!!
        product["imgUrls"] = emptyList<String>()
        product["qtyAvailable"] = quantityAvailable.value!!.toInt()
        product["qtySold"] = 0
        product["priceStr"] = "${currency.value!!.currencyCode}-${price.value!!}"
        product["location"] = _location.value!!

        val myProduct = Product(
            name = title.value!!,
            description = description.value!!,
            sellerId = user!!.uid,
            type = category.value!!,
            imgUrls = emptyList(),
            qtyAvailable = quantityAvailable.value!!.toInt(),
            qtySold = 0,
            priceStr = "${currency.value!!.currencyCode}-${price.value!!}",
            location = _location.value!!
        )

        val collection = FirebaseFirestore.getInstance().collection("ads")
//        collection.add(product)
        collection.add(myProduct)
            .addOnCompleteListener {
                finishLoading()
                if (it.isSuccessful) {
                    // notify of success or failure
                    postNotification("Ad posted!")
                    _events.postValue(Event(Loads.NAVIGATE_PRODUCT))

                } else {
                    // notify of error
                }
            }.addOnSuccessListener {
                documentAddress = collection.document(it.id)
                uiScope.launch {

                    withContext(Dispatchers.IO) { uploadImageToFirebaseStorage() }
                }
            }
    }

    private fun postNotification(message: String) {
        _eventNotification.postValue(Event(message))
    }

    private fun uploadImageToFirebaseStorage() {
        if (imageUriList.size > 0) {
            val imageUri = imageUriList[0]
            val storageRef = firebaseStorageRef
                .child("${UUID.randomUUID()}.${getFileExtension(imageUri.toString())}")

            imageUriList.removeAt(0)
            currentPictureUpload += 1

            val uploadTask = storageRef.putFile(imageUri)
            postNotification("Uploading image $currentPictureUpload/$pictureCount")
            uploadTask.continueWithTask { task ->

                if (!task.isSuccessful) {
                    postNotification("Image $currentPictureUpload/$pictureCount upload failed!")
                    throw task.exception!!
                }

                storageRef.downloadUrl

            }.addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    documentAddress.update("imgUrls", FieldValue.arrayUnion(task.result.toString()))
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

@Suppress("UNCHECKED_CAST")
class AddProductViewModelFactory(private val repository: IRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (ModifyAdViewModel(repository) as T)
}