package com.ilatyphi95.farmersmarket.ui.message

import androidx.lifecycle.*
import com.ilatyphi95.farmersmarket.data.entities.Message
import com.ilatyphi95.farmersmarket.data.repository.IRepository
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem
import com.ilatyphi95.farmersmarket.utils.Event
import com.ilatyphi95.farmersmarket.utils.MessageItemViewModel
import com.ilatyphi95.farmersmarket.utils.toRecyclerItem

class MessageViewModel(val repository: IRepository) : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>()
    val messages : LiveData<List<RecyclerItem>> = _messages.map { list ->
        list.filter{ it.message.isNotEmpty() }.map {message ->
            MessageItemViewModel(message).apply {
                itemClickHandler = { messageClicked(message.id) }

            }.toRecyclerItem()
        }
    }

    private val _eventMessage = MutableLiveData<Event<String>>()
    val eventMessage : LiveData<Event<String>>
        get() = _eventMessage

    fun updateMessages(messageList: List<Message>) {
        _messages.postValue(messageList)
    }

    private fun messageClicked(messageId: String) {
        _eventMessage.value = Event(messageId)
    }
}

@Suppress("UNCHECKED_CAST")
class MessageViewModelFactory(val repository: IRepository) : ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (MessageViewModel(repository) as T)
}
