package com.ilatyphi95.farmersmarket.ui.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel()  {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    fun startLoading(){
        _isLoading.value = true
    }

    fun stopLoading(){
        _isLoading.value = false
    }

}