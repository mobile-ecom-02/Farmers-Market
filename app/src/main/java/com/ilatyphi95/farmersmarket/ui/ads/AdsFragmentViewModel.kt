package com.ilatyphi95.farmersmarket.ui.ads

import androidx.lifecycle.*
import com.google.firebase.Timestamp
import com.ilatyphi95.farmersmarket.data.entities.AdItem
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem
import com.ilatyphi95.farmersmarket.firebase.services.ProductServices
import com.ilatyphi95.farmersmarket.utils.Event
import com.ilatyphi95.farmersmarket.utils.InterestedAdsViewModel
import com.ilatyphi95.farmersmarket.utils.PostedAdsViewModel
import com.ilatyphi95.farmersmarket.utils.toRecyclerItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.*

const val NEW_PRODUCT = "New Product"

@ExperimentalCoroutinesApi
class AdsFragmentViewModel(private val service: ProductServices) : ViewModel() {

    private val _eventAdsDetails = MutableLiveData<Event<Product>>()
    val eventAdsDetails: LiveData<Event<Product>>
        get() = _eventAdsDetails

    private val _eventEditAds = MutableLiveData<Event<String>>()
    val eventEditAds: LiveData<Event<String>>
        get() = _eventEditAds

    val postedAdsList: LiveData<List<RecyclerItem>> = service.myItems().asLiveData().map { list ->
        list.map { item ->
            val timestamp = if (item.location != null) {
                Timestamp(Date(item.location.time))
            } else Timestamp.now()

            PostedAdsViewModel(
                AdItem(
                    itemId = item.id,
                    name = item.name,
                    price = item.priceStr,
                    quantity = item.qtyAvailable,
                    imageUrl = item.imgUrls.getOrElse(0) { "" },
                    timestamp = timestamp
                )
            ).apply {
                itemClickHandler = { postedItemClicked(itemId = this.item.itemId) }
            }.toRecyclerItem()
        }
    }

    val interestedAdsList: LiveData<List<RecyclerItem>> = service.interestedItems().asLiveData().map { list ->
        list.map { item ->
            InterestedAdsViewModel(item).apply {
                itemClickHandler = { interestedAddClicked(itemId = this.item.itemId) }
            }.toRecyclerItem()
        }
    }

    private fun interestedAddClicked(itemId: String) {
        viewModelScope.launch {
            service.getProduct(itemId)?.let { _eventAdsDetails.postValue(Event(it)) }
        }
    }

    private fun postedItemClicked(itemId: String) {
        _eventEditAds.value = Event(itemId)
    }

    fun postNewAd() {
        _eventEditAds.value = Event(NEW_PRODUCT)
    }
}

@Suppress("UNCHECKED_CAST")
@ExperimentalCoroutinesApi
class AdsFragmentViewModelFactory(private val services: ProductServices) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (AdsFragmentViewModel(services) as T)
}