package com.ilatyphi95.farmersmarket.data.repository

import android.net.Uri
import androidx.lifecycle.liveData
import com.google.firebase.Timestamp
import com.ilatyphi95.farmersmarket.data.entities.*
import com.ilatyphi95.farmersmarket.data.repository.ProductGenerator.generateLocation
import com.ilatyphi95.farmersmarket.utils.longToLocalDateTime
import com.ilatyphi95.farmersmarket.utils.toLocation
import com.ilatyphi95.farmersmarket.utils.toTimeStamp
import com.thedeanda.lorem.LoremIpsum
import kotlinx.coroutines.delay
import kotlin.random.Random

class SampleRepository : IRepository {

    private val lorem = LoremIpsum.getInstance()
    private val chatList = mutableListOf<ChatMessage>()

    override fun searchProducts(searchString: String) = liveData {
        delay(1000)
        ProductGenerator.resetList(10)
        emit(ProductGenerator.getList())
    }

    override suspend fun getUser(sellerId: String): User {
        delay(3000)
        return User(
            id = sellerId,
            email = "ilatyphi95@gmail.com",
            firstName = "AbdulLateef",
            lastName = "Opebiyi",
            phone = "08038057735",
            profileDisplayName = "ilatyphi95",
            profilePicUrl = "https://www.eatforhealth.gov.au/sites/default/files/images/the_guidelines/fruit_selection_155265101_web.jpg"
        )
    }

    override suspend fun getRecentProducts(): List<Product> {
        delay(1000)
        return ProductGenerator.resetList(15)
    }

    override suspend fun getCloseByProduct(): List<CloseByProduct> {
        val list = mutableListOf<CloseByProduct>()
        val thisLocation = generateLocation().toLocation()
        ProductGenerator.resetList(40).forEach {
            list.add(CloseByProduct(it, thisLocation.distanceTo(it.location?.toLocation())))
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
        return User(
            id = "AbdulLateefOpebiyi",
            email = "ilatyphi95@gmail.com",
            firstName = "AbdulLateef",
            lastName = "Opebiyi",
            phone = "08038057735",
            profileDisplayName = "ilatyphi95"
        )
    }

    override fun insertProduct(product: Product) {

    }

    override fun getMessages(messageId: String) = liveData<List<ChatMessage>> {
        for (i in 1..10) {
            delay(Random.nextLong(1000, 10000))
            emit(generateChat(messageId))
        }
    }

    override suspend fun getMessageRecipients(messageId: String): List<String> {
        return listOf(getCurrentUser().id, "ade")
    }

    override fun sendMessage(chatMessage: ChatMessage) {
        chatList.add(chatMessage)
    }

    override suspend fun getPostedAds(): List<AdItem> {
        return randomAddItem()
    }

    override suspend fun getInterestedAds(): List<AdItem> {
        return randomAddItem()
    }

    override suspend fun getAd(itemId: String): Product {
        return ProductGenerator.getList()[0]
    }

    override suspend fun getMessageList(): List<Message>? {
        return randomMessages()
    }

    private fun randomMessages(): List<Message>? {
        val list = mutableListOf<Message>()
        val total = Random.nextInt(10, 50)

        for (count in 1..total) {
            list.add(
                Message(
                    id = lorem.firstNameFemale + lorem.firstNameMale,
                    message = lorem.getWords(3, 12),
                    senderID = lorem.firstNameMale + lorem.firstNameFemale,
                    imageUrl = ProductGenerator.generateImage(),
                    senderName = lorem.firstName,
                    timestamp = longToLocalDateTime(
                        System.currentTimeMillis() + Random.nextLong(-3000000000, 0)).toTimeStamp()
                )
            )
        }

        return list
    }

    private fun randomAddItem(): MutableList<AdItem> {
        val list = mutableListOf<AdItem>()
        val total = Random.nextInt(3, 35)

        for (count in 1..total) {
            list.add(
                AdItem(
                    name = lorem.name, quantity = Random.nextInt(10, 50),
                    price = "NGN-${Random.nextInt(10, 500)}", itemId = lorem.firstNameFemale,
                    timestamp = longToLocalDateTime(
                        System.currentTimeMillis() + Random.nextLong(-3000000000, 0)).toTimeStamp(),
                    imageUrl = ProductGenerator.generateImage()
                )
            )
        }
        return list
    }

    private fun generateChat(messageId: String): List<ChatMessage> {

        chatList.add(
            ChatMessage(
                chatId = messageId,
                msg = lorem.getParagraphs(1, 1),
                senderId = "ade",
                timeStamp = Timestamp.now()
            )
        )
        return chatList
    }
}