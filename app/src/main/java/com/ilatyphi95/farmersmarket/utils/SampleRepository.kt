package com.ilatyphi95.farmersmarket.utils

import android.net.Uri
import androidx.lifecycle.liveData
import com.ilatyphi95.farmersmarket.IRepository
import com.ilatyphi95.farmersmarket.ProductGenerator
import com.ilatyphi95.farmersmarket.data.entities.ChatMessage
import com.ilatyphi95.farmersmarket.data.entities.CloseByProduct
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.entities.User
import com.thedeanda.lorem.LoremIpsum
import kotlinx.coroutines.delay
import kotlin.random.Random

class SampleRepository : IRepository {

    private val lorem = LoremIpsum.getInstance()
    private val chatList = mutableListOf<ChatMessage>()

    override fun searchProducts(searchString: String)  = liveData {
        delay(1000)
        ProductGenerator.resetList(10)
        emit(ProductGenerator.getList())
    }

    override suspend fun getUser(sellerId: String): User {
        delay(3000)
        return User(id = sellerId, email = "ilatyphi95@gmail.com", firstName = "AbdulLateef",
        lastName = "Opebiyi", phone = "08038057735", location = "Nigeria", profileDisplayName = "ilatyphi95",
        profilePicUrl = "https://www.eatforhealth.gov.au/sites/default/files/images/the_guidelines/fruit_selection_155265101_web.jpg")
    }

    override suspend fun getRecentProducts(): List<Product> {
        delay(1000)
        return ProductGenerator.resetList(15)
    }

    override suspend fun getCloseByProduct(): List<CloseByProduct> {
        val list = mutableListOf<CloseByProduct>()
        ProductGenerator.resetList(40).forEach {
            list.add(CloseByProduct(it, Random.nextInt(1, 40)))
        }
        return list.sortedBy { it.distance }
    }

    override suspend fun getCategory(): List<String> {
        delay(2000)
        return listOf("Crop", "Livestock", "Poultry")
    }

    override suspend fun uploadPicture(file: Uri?): String {
        return lorem.firstName
    }

    override fun getCurrentUser(): User {
        return User(id = "AbdulLateefOpebiyi", email = "ilatyphi95@gmail.com", firstName = "AbdulLateef",
            lastName = "Opebiyi", phone = "08038057735", location = "Nigeria", profileDisplayName = "ilatyphi95")
    }

    override fun insertProduct(product: Product) {

    }

    override fun getMessages(messageId: String) = liveData<List<ChatMessage>> {
        for(i in 1..10) {
            delay(Random.nextLong(1000,10000))
            emit(generateChat(messageId))
        }
    }

    override suspend fun getMessageRecipients(messageId: String): List<String> {
        return listOf(getCurrentUser().id, "ade")
    }

    override fun sendMessage(chatMessage: ChatMessage) {
        chatList.add(chatMessage)
    }

    private fun generateChat(messageId: String) : List<ChatMessage> {

        chatList.add(ChatMessage(
            chatId = messageId,
            msg = lorem.getParagraphs(1,1),
            senderId = "ade",
            timeStamp = System.currentTimeMillis()
        ))
        return chatList
    }
}