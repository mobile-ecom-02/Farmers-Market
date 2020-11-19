package com.ilatyphi95.farmersmarket.ui.chat

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.ilatyphi95.farmersmarket.data.entities.ChatMessage
import com.ilatyphi95.farmersmarket.data.entities.MessageShell
import com.ilatyphi95.farmersmarket.data.entities.User
import com.ilatyphi95.farmersmarket.utils.ReceiveRecyclerViewModel
import com.ilatyphi95.farmersmarket.utils.SentRecyclerViewModel
import com.ilatyphi95.farmersmarket.utils.toRecyclerItem

class ChatFragmentViewModel(messageId: String)
    : ViewModel() {

    private val firestoreRef = FirebaseFirestore.getInstance()

    private val documentRef = firestoreRef.document("messages/$messageId")

    private val thisUser = FirebaseAuth.getInstance().currentUser

    private val _otherUser = MutableLiveData<User>()
    val otherUser : LiveData<User>
        get() = _otherUser

    val newMessage = MutableLiveData<String>()

    private val chatMessages = MutableLiveData<List<ChatMessage>>()
    val chatRecycler = chatMessages.map { messageList ->
        messageList.map {
            if(it.senderId == thisUser!!.uid) {
                SentRecyclerViewModel(it).toRecyclerItem()
            } else {
                ReceiveRecyclerViewModel(it).toRecyclerItem()
            }
        }
    }


    init {
        documentRef.get().addOnSuccessListener {document ->

            val messageShell = document.toObject<MessageShell>()
            val otherUserId = messageShell!!.participants.find{it != thisUser!!.uid}!!

            firestoreRef.document("users/$otherUserId").get().addOnSuccessListener {
                _otherUser.postValue(it.toObject())
            }

        }
    }

    fun sendMessage() {
        val message = newMessage.value

        if(message.isNullOrEmpty()) {
            return
        }

        documentRef.collection("chatMessages").add(ChatMessage(
            msg = message,
            senderId = thisUser!!.uid
        ))
        newMessage.postValue("")
    }

    fun updateChat(chatList: List<ChatMessage>) {
        chatMessages.postValue(chatList.sortedBy { it.timeStamp })
    }
}

@Suppress("UNCHECKED_CAST")
class ChatFragmentViewModelFactory(private val messageId: String)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        (ChatFragmentViewModel(messageId) as T)
}