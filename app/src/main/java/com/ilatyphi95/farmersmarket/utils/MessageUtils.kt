package com.ilatyphi95.farmersmarket.utils

import androidx.annotation.LayoutRes
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.BR
import com.ilatyphi95.farmersmarket.data.entities.ChatMessage
import com.ilatyphi95.farmersmarket.data.entities.Message
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem

const val DATE_SEPARATOR = "this-is-date-separator"

sealed class ChatRecyclerViewModel(val chatMessage: ChatMessage, @LayoutRes val layoutId: Int)

class  SentRecyclerViewModel(val chat: ChatMessage)
    : ChatRecyclerViewModel(chat, R.layout.chat_message)

class  ReceiveRecyclerViewModel(val chat: ChatMessage)
    : ChatRecyclerViewModel(chat, R.layout.chat_message_user)

class DateGroupRecyclerViewModel(val chat: ChatMessage)
    : ChatRecyclerViewModel(chat, R.layout.message_time_separator)

fun ChatRecyclerViewModel.toRecyclerItem() = RecyclerItem(
    data = chatMessage,
    layoutId = layoutId,
    variableId = BR.viewmodel
)

class MessageItemViewModel(val message: Message) {
    lateinit var itemClickHandler: (messageId: String) -> Unit

    fun onItemClick() {
        itemClickHandler(message.id)
    }
}

fun MessageItemViewModel.toRecyclerItem() = RecyclerItem(
    data = this,
    layoutId = R.layout.message_item,
    variableId = BR.viewModel
)