package com.ilatyphi95.farmersmarket

import android.net.Uri
import androidx.lifecycle.*
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.entities.User
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem
import com.ilatyphi95.farmersmarket.utils.*
import kotlinx.coroutines.*
import java.util.*

enum class Loads {
    ADD_PICTURES,
    LOAD_CURRENCY,
    LOAD_CATEGORY,
    NAVIGATE_PRODUCT
}

class AddProductViewModel(private val repository: IRepository) : ViewModel() {
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

    private val _address = MutableLiveData<List<String>>()
    val address: LiveData<String> = _address.map {
            "${it[2]}, ${it[4]}"
    }

    var loadedList: List<String>? = emptyList()
    private lateinit var currencies: List<Currency>

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading


    private val _events = MutableLiveData<Event<Loads>>()
    val events: LiveData<Event<Loads>>
        get() = _events


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

    fun updateAddress(address: String) {
        _address.value = address.split(SEPARATOR)
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    fun postAd() {
        _isLoading.value = true
        val list = _pictures.value!!
        val pictureUrl = list.subList(1, list.lastIndex)
        val storageRef = mutableListOf<String>()

        uiScope.launch {
            withContext(Dispatchers.IO) {

                for(item in pictureUrl) {
                    storageRef.add(repository.uploadPicture(item))
                }

                repository.insertProduct(
                    Product(name = title.value!!,
                        description = description.value!!,
                        sellerId = user.id,
                        type = category.value!!,
                        qtyAvailable = quantityAvailable.value!!.toInt(),
                        qtySold = 0,
                        priceStr = "${currency.value!!.currencyCode}-${price.value!!}"
                    )
                )

                finishLoading()
                // notify of success or failure
                _events.postValue(Event(Loads.NAVIGATE_PRODUCT))
            }

        }
    }

    fun setCategory(string: String) {
        _category.value = string
    }
}

@Suppress("UNCHECKED_CAST")
class AddProductViewModelFactory(private val repository: IRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (AddProductViewModel(repository) as T)
}