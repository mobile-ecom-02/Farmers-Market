package com.ilatyphi95.farmersmarket.ui.ads

import androidx.lifecycle.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import com.ilatyphi95.farmersmarket.data.entities.AdItem
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.repository.IRepository
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem
import com.ilatyphi95.farmersmarket.utils.Event
import com.ilatyphi95.farmersmarket.utils.InterestedAdsViewModel
import com.ilatyphi95.farmersmarket.utils.PostedAdsViewModel
import com.ilatyphi95.farmersmarket.utils.toRecyclerItem
import kotlinx.coroutines.*
import java.util.*

const val NEW_PRODUCT = "New Product"
class AdsFragmentViewModel(private val repository: IRepository) : ViewModel() {

    private val job = Job()
    private val uiScope = CoroutineScope(job + Dispatchers.Main)

    private val _eventAdsDetails = MutableLiveData<Event<Product>>()
    val eventAdsDetails : LiveData<Event<Product>>
        get() = _eventAdsDetails

    private val _eventEditAds = MutableLiveData<Event<String>>()
    val eventEditAds : LiveData<Event<String>>
        get() = _eventEditAds

    private val _postedAdsList = MutableLiveData<List<Product>>()
    val postedAdsList: LiveData<List<RecyclerItem>> = _postedAdsList.map { list ->
        list.map { item ->
            val timestamp = if(item.location != null ){
                Timestamp(Date(item.location.time))
            } else Timestamp.now()

                PostedAdsViewModel(
                    AdItem(
                    itemId = item.id,
                        name = item.name,
                        price = item.priceStr,
                        quantity = item.qtyAvailable,
                        imageUrl = item.imgUrls.getOrElse(0){""},
                        timestamp = timestamp
                )).apply {
                    itemClickHandler = { postedItemClicked(itemId = this.item.itemId) }
                }.toRecyclerItem()
        }
    }

    private val _interestedAdsList = MutableLiveData<List<AdItem>>()
    val interestedAdsList : LiveData<List<RecyclerItem>> = _interestedAdsList.map { list ->
        list.map { item ->
            InterestedAdsViewModel(item).apply {
                itemClickHandler = { interestedAddClicked(itemId = this.item.itemId) }
            }.toRecyclerItem()
        }
    }

    private fun interestedAddClicked(itemId: String) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                FirebaseFirestore.getInstance().collection("ads")
                    .whereEqualTo(FieldPath.documentId(), itemId).limit(1)
                    .get()
                    .continueWithTask { task ->
                        if(task.isSuccessful) {
                            task.result.toObjects<Product>().let {
                                _eventAdsDetails.postValue(Event(it[0]))
                            }
                        }
                        task
                    }
            }
        }

    }

    private fun postedItemClicked(itemId: String) {
        _eventEditAds.value = Event(itemId)

    }

    fun postNewAd() {
        _eventEditAds.value = Event(NEW_PRODUCT)

    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    fun upDatePostedAds(products: List<Product>) {
        _postedAdsList.postValue(products)
    }

    fun upDateInterestedAds(items: List<AdItem>) {
        _interestedAdsList.postValue(items)

    }

}

@Suppress("UNCHECKED_CAST")
class AdsFragmentViewModelFactory(private val repository: IRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (AdsFragmentViewModel(repository) as T)
}