package com.ilatyphi95.farmersmarket.utils

import androidx.annotation.LayoutRes
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.BR
import com.ilatyphi95.farmersmarket.data.entities.ChatMessage
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem

sealed class ChatRecyclerViewModel(val chatMessage: ChatMessage, @LayoutRes val layoutId: Int)

class  SentRecyclerViewModel(val chat: ChatMessage)
    : ChatRecyclerViewModel(chat, R.layout.chat_message)

class  ReceiveRecyclerViewModel(val chat: ChatMessage)
    : ChatRecyclerViewModel(chat, R.layout.chat_message_user)

fun ChatRecyclerViewModel.toRecyclerItem() = RecyclerItem(
    data = chatMessage,
    layoutId = layoutId,
    variableId = BR.viewmodel
)