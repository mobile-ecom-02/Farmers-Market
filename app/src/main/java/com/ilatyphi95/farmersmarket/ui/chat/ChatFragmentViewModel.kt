package com.ilatyphi95.farmersmarket.ui.chat

import androidx.lifecycle.*
import com.ilatyphi95.farmersmarket.data.entities.ChatMessage
import com.ilatyphi95.farmersmarket.data.entities.User
import com.ilatyphi95.farmersmarket.data.repository.IRepository
import com.ilatyphi95.farmersmarket.utils.ReceiveRecyclerViewModel
import com.ilatyphi95.farmersmarket.utils.SentRecyclerViewModel
import com.ilatyphi95.farmersmarket.utils.toRecyclerItem
import kotlinx.coroutines.*

class ChatFragmentViewModel(private val messageId: String, private val repository: IRepository)
    : ViewModel() {

    private val job = Job()
    private val uiScope = CoroutineScope(job + Dispatchers.Main)

    private val thisUser = repository.getCurrentUser()

    private val _otherUser = MutableLiveData<User>()
    val otherUser : LiveData<User>
        get() = _otherUser

    val newMessage = MutableLiveData<String>()
    val enableSend = newMessage.map {
        !it.isNullOrBlank()
    }

    private val chatMessages = repository.getMessages(messageId)
    val chatRecycler = chatMessages.map { messageList ->
        messageList.map {
            if(it.senderId == thisUser.id) {
                SentRecyclerViewModel(it).toRecyclerItem()
            } else {
                ReceiveRecyclerViewModel(it).toRecyclerItem()
            }
        }
    }


    init {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val users = repository.getMessageRecipients(messageId)
                val otherUserId = users.find { it != thisUser.id }

                otherUserId.isNullOrBlank()
                otherUserId?.let {
                    _otherUser.postValue(repository.getUser(it))
                }

            }
        }
    }

    fun sendMessage() {
        repository.sendMessage(ChatMessage(
            chatId = messageId,
            msg = newMessage.value!!,
            senderId = thisUser.id,
            timeStamp = System.currentTimeMillis()
            ))
        newMessage.value = ""
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }
}

@Suppress("UNCHECKED_CAST")
class ChatFragmentViewModelFactory(private val messageId: String, private val repository: IRepository)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (ChatFragmentViewModel(messageId, repository) as T)
}