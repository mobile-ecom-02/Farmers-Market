package com.ilatyphi95.farmersmarket.ui.chat

import androidx.lifecycle.*
import com.ilatyphi95.farmersmarket.data.entities.ChatMessage
import com.ilatyphi95.farmersmarket.data.entities.User
import com.ilatyphi95.farmersmarket.firebase.services.ProductServices
import com.ilatyphi95.farmersmarket.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ChatFragmentViewModel(val messageId: String, private val service: ProductServices) :
    ViewModel() {

    private val thisUserUid = service.getThisUserUid()

    private val _otherUser = MutableLiveData<User>()
    val otherUser: LiveData<User>
        get() = _otherUser

    val newMessage = MutableLiveData<String>()

    private val chatMessages = service.readChats(messageId).asLiveData()

    val chatRecycler = chatMessages.map { list ->
        val myList = mutableListOf<ChatMessage>()

        list.groupBy {
            it.timeStamp?.toLocalDateTime()?.toLocalDate()
        }.forEach { map ->
            map.value[0].timeStamp?.let {
                myList.add(ChatMessage(senderId = DATE_SEPARATOR, timeStamp = it))
            }
            myList.addAll(map.value)
        }

        myList.map {
            when (it.senderId) {
                DATE_SEPARATOR -> { DateGroupRecyclerViewModel(it).toRecyclerItem() }
                thisUserUid -> { SentRecyclerViewModel(it).toRecyclerItem() }
                else -> { ReceiveRecyclerViewModel(it).toRecyclerItem() }
            }
        }
    }


    init {
        viewModelScope.launch {
            _otherUser.postValue(service.getOtherUser(messageId))
        }
    }

    fun sendMessage() {
        val message = newMessage.value

        if (message.isNullOrEmpty()) {
            return
        }

        service.addChat(messageId, message)

        newMessage.postValue("")
    }
}

@ExperimentalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class ChatFragmentViewModelFactory(
    private val messageId: String,
    private val service: ProductServices
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (ChatFragmentViewModel(messageId, service) as T)
}