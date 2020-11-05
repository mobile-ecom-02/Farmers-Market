package com.ilatyphi95.farmersmarket.ui.ads

import androidx.lifecycle.*
import com.ilatyphi95.farmersmarket.data.repository.IRepository
import com.ilatyphi95.farmersmarket.data.entities.AddItem
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem
import com.ilatyphi95.farmersmarket.utils.Event
import com.ilatyphi95.farmersmarket.utils.InterestedAdsViewModel
import com.ilatyphi95.farmersmarket.utils.PostedAdsViewModel
import com.ilatyphi95.farmersmarket.utils.toRecyclerItem
import kotlinx.coroutines.*

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

    private val _postedAdsList = MutableLiveData<List<AddItem>>()
    val postedAdsList: LiveData<List<RecyclerItem>> = _postedAdsList.map { list ->
        list.map { item ->
            PostedAdsViewModel(item).apply {
                itemClickHandler = { postedItemClicked(itemId = this.item.itemId) }
            }.toRecyclerItem()
        }
    }

    private val _interestedAdsList = MutableLiveData<List<AddItem>>()
    val interestedAdsList : LiveData<List<RecyclerItem>> = _interestedAdsList.map { list ->
        list.map { item ->
            InterestedAdsViewModel(item).apply {
                itemClickHandler = { interestedAddClicked(itemId = this.item.itemId) }
            }.toRecyclerItem()
        }
    }

    init {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                _postedAdsList.postValue(repository.getPostedAds())
                _interestedAdsList.postValue(repository.getInterestedAds())
            }
        }
    }

    private fun interestedAddClicked(itemId: String) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                _eventAdsDetails.postValue(Event(repository.getAd(itemId)))
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

}

@Suppress("UNCHECKED_CAST")
class AdsFragmentViewModelFactory(private val repository: IRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (AdsFragmentViewModel(repository) as T)
}