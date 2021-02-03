package com.ilatyphi95.farmersmarket.ui.settings

import android.app.Application
import android.net.Uri
import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.data.entities.User
import com.ilatyphi95.farmersmarket.firebase.services.ProductServices
import com.ilatyphi95.farmersmarket.utils.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class UserAccountViewModel(application: Application, val services: ProductServices) : AndroidViewModel(application) {
    private var saveUserData: User? = null
    val updatedUserData = MutableLiveData(User())

    val location: LiveData<String> = updatedUserData.map {
        val address = it.location
        "${address?.city}, ${address?.state}"
    }

    private val _isLoadingImage = MutableLiveData(false)
    val isLoadingImage: LiveData<Boolean> = _isLoadingImage

    private val _eventCloseAccount = MutableLiveData<Event<String>>()
    val eventCloseAccount: LiveData<Event<String>> = _eventCloseAccount

    private val _isTrackingLocation = MutableLiveData(false)

    val loadingImage: LiveData<Int> = _isTrackingLocation.map {
        if(it) {
            R.drawable.ic_baseline_my_location_24
        } else {
            R.drawable.ic_baseline_location_searching_24
        }
    }

    private val _eventMessage = MutableLiveData<Event<@StringRes Int>>()
    val eventMessage: LiveData<Event<Int>> = _eventMessage

    private val oldPictureList = mutableListOf<String>()

    val updateButtonState: LiveData<Int> = updatedUserData.map {
        if(it == saveUserData) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    val showLoadingImage = _isLoadingImage.map {
        if(it) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    init {
        viewModelScope.launch {
            saveUserData = services.getUser(services.getThisUserUid()!!)
            updatedUserData.value = saveUserData
        }
    }

    fun update() {
        updatedUserData.value?.let {
            viewModelScope.launch {
                val message =
                if(services.updateUser(it)) {
                    clearImages()
                    _eventCloseAccount.value = Event("close account")
                    // notify the ui
                    R.string.update_successful
                } else {
                    R.string.update_failed
                }
                showNotification(message)
            }
        }
    }

    fun updateUser(user: User) {
        updatedUserData.value = user
    }

    fun uploadPicture(pictureUri: Uri?) {
        pictureUri?.let {
            _isLoadingImage.postValue(true)
            viewModelScope.launch {
                val uploadUri = services.uploadImage(pictureUri)
                uploadUri?.let {
                    val user = updatedUserData.value!!
                    oldPictureList.add(user.profilePicUrl)
                    updateUser(user.copy(profilePicUrl = it))
                }
                _isLoadingImage.postValue(false)
            }
        }
    }

    fun toggleLocation() {
        _isTrackingLocation.value = _isTrackingLocation.value?.not()
    }

    fun resetImages() {
        val currentUserState = updatedUserData.value
        if(currentUserState?.profilePicUrl != saveUserData?.profilePicUrl) {
            currentUserState?.let {
                oldPictureList.add(it.profilePicUrl)
            }
        }
        clearImages()
    }

    private fun clearImages() {
        viewModelScope.launch {
            services.removeImages(getApplication(), oldPictureList)
        }
    }

    override fun onCleared() {
        resetImages()
        super.onCleared()
    }

    fun showNotification(@StringRes message: Int) {
        _eventMessage.value = Event(message)
    }
}


@ExperimentalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class UserAccountViewModelFactory(
    private val application: Application, private val service: ProductServices) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (UserAccountViewModel(application, service) as T)
}

