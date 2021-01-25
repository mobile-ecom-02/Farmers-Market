package com.ilatyphi95.farmersmarket.firebase.services

import android.net.Uri
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.common.io.Files.getFileExtension
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.ilatyphi95.farmersmarket.data.ProductPagingSource
import com.ilatyphi95.farmersmarket.data.entities.*
import com.koalap.geofirestore.GeoFire
import com.koalap.geofirestore.GeoLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@ExperimentalCoroutinesApi
object ProductServices {

    private const val TAG = "ProductServices"
    private val db = FirebaseFirestore.getInstance()

    fun getThisUserUid(): String? {
        return FirebaseAuth.getInstance().uid
    }

    suspend fun searchProduct(keyword: String): List<Product> {

        return try {
            db.collection("ads").whereArrayContains("keywords", keyword)
                .whereNotEqualTo("sellerId", getThisUserUid())
                .limit(30).get().await()
                .documents.mapNotNull { it.toObject<Product>() }

        } catch (e: Exception) {
            Log.e(TAG, "Error getting ads", e)
            FirebaseCrashlytics.getInstance().log("Error getting ads")
            FirebaseCrashlytics.getInstance().recordException(e)
            emptyList()
        }
    }

    suspend fun createMessage(product: Product): String? {
        val buyerId = getThisUserUid()!!
        val messageId =
            product.id.substring(0, 10) + product.sellerId.substring(0, 10) + buyerId.substring(
                0,
                10
            )

        val messageShell = MessageShell(
            productId = product.id,
            imgUrl = product.imgUrls[0],
            participants = listOf(product.sellerId, buyerId)
        )

        var successfulInsert = false
        db.document("messages/${messageId}")
            .set(messageShell, SetOptions.merge())
            .addOnCompleteListener {
                successfulInsert = it.isSuccessful
            }
            .await()

        return if (successfulInsert) {
            messageId
        } else {
            null
        }
    }

    suspend fun getUser(sellerId: String): User? {
        return try {
            db.document("users/${sellerId}").get()
                .await().toObject<User>()
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Error getting User", e)
            FirebaseCrashlytics.getInstance().log("Error getting User")
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }

    suspend fun getOtherUser(messageId: String): User? {
        return try {
            val otherUserId = db.document("messages/${messageId}").get()
                .await().toObject<MessageShell>()!!
                .participants.find { it != FirebaseAuth.getInstance().uid }

            getUser(otherUserId!!)

        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Error getting Message: $messageId", e)
            FirebaseCrashlytics.getInstance().log("Error getting Message: $messageId")
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }

    fun addChat(messageId: String, message: String) {
        db.collection("messages/$messageId/chatMessages").add(
            ChatMessage(
                msg = message,
                senderId = FirebaseAuth.getInstance().uid!!
            )
        )
    }

    suspend fun getProduct(productId: String): Product? {
        return try {
            db.document("ads/$productId")
                .get().await()
                .toObject()!!
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Error getting ad: $productId", e)
            FirebaseCrashlytics.getInstance().log("Error getting ad: $productId")
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }


    fun interestedItems(): Flow<List<AdItem>> {

        return callbackFlow {
            val listener = db.document("users/${getThisUserUid()}")
                .collection("interestedItems").addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        cancel(message = "Error fetching interested items", cause = exception)
                        return@addSnapshotListener
                    }
                    val interestedItems = snapshot!!.documents.mapNotNull { it.toObject<AdItem>() }

                    offer(interestedItems)
                }
            awaitClose {
                Log.d(TAG, "Cancelling interestedItems listener")
                listener.remove()
            }
        }
    }


    fun recentItems(): Flow<List<AdItem>> {

        return callbackFlow {
            val listener = db.document("users/${getThisUserUid()}")
                .collection("recent").addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        cancel(message = "Error fetching recent items", cause = exception)
                        return@addSnapshotListener
                    }
                    val interestedItems = snapshot!!.documents.mapNotNull { it.toObject<AdItem>() }

                    offer(interestedItems)
                }
            awaitClose {
                Log.d(TAG, "Cancelling recentItems listener")
                listener.remove()
            }
        }
    }

    fun myItems(): Flow<List<Product>> {

        return callbackFlow {
            val listener = db.collection("ads").whereEqualTo("sellerId", getThisUserUid())
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        cancel(message = "Error fetching interested items", cause = exception)
                        return@addSnapshotListener
                    }
                    val interestedItems = snapshot!!.documents.mapNotNull { it.toObject<Product>() }

                    offer(interestedItems)
                }
            awaitClose {
                Log.d(TAG, "Cancelling interestedItems listener")
                listener.remove()
            }
        }
    }

    fun getMessageList(): Flow<List<Message>> {

        return callbackFlow {
            val listener = db.collection("users/${getThisUserUid()}/chatList")
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        cancel(message = "Error fetching interested items", cause = exception)
                        return@addSnapshotListener
                    }
                    val interestedItems = snapshot!!.documents.mapNotNull { it.toObject<Message>() }

                    offer(interestedItems)
                }
            awaitClose {
                Log.d(TAG, "Cancelling interestedItems listener")
                listener.remove()
            }
        }
    }

    suspend fun closeByItems(): List<Product> {
        val user = getUser(getThisUserUid()!!)

        return suspendCoroutine { continuation ->

            user?.location?.let { location ->
                val ref = db.collection("ads")

                GeoFire(ref, ref.limit(30))
                    .queryAtLocation(
                        GeoLocation(location.latitude, location.longitude), 100.0
                    )
                    .addGeoQueryForSingleValueEvent { list ->

                        continuation.resume(
                            list.map { docChange -> docChange.document.toObject() }
                        )
                    }
            }
        }
    }

    fun readChats(messageId: String): Flow<List<ChatMessage>> {

        return callbackFlow {
            val listener = db.collection("messages/${messageId}/chatMessages")
                .orderBy("timeStamp")
                .addSnapshotListener { snapshot, exception ->

                    if (exception != null) {
                        cancel(message = "Error fetching Chats for: $messageId", cause = exception)
                        return@addSnapshotListener
                    }

                    val chatMessages =
                        snapshot!!.documents.mapNotNull { it.toObject<ChatMessage>() }

                    offer(chatMessages)

                    // reset counter
                    val map = HashMap<String, Int>()
                    map["counter"] = 0

                    db.document("users/${getThisUserUid()}/chatList/${messageId}")
                        .set(map, SetOptions.merge())
                }
            awaitClose {
                Log.d(TAG, "Cancelling Chats listener")
                listener.remove()
            }
        }
    }

    suspend fun getCategories(): List<Category> {

        return try {
            db.collection("categories").get()
                .await().documents.mapNotNull { it.toObject() }

        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Error getting Categories", e)
            FirebaseCrashlytics.getInstance().log("Error getting Categories")
            FirebaseCrashlytics.getInstance().recordException(e)
            emptyList()
        }
    }

    suspend fun uploadAd(product: Product): String? {

        var productId: String? = null

        db.collection("ads").add(product).addOnCompleteListener {
            if (it.isSuccessful) {
                productId = it.result.id
                product.location?.let { location ->
                    val geoFire = GeoFire(db.collection("ads"))
                    geoFire.setLocation(
                        productId,
                        GeoLocation(location.latitude, location.longitude)
                    )
                }
            }
        }.await()

        return productId
    }

    suspend fun upDateAd(product: Product): String? {

        var productId: String? = null

        db.document("ads/${product.id}")
            .set(product, SetOptions.merge()).addOnCompleteListener {
                if (it.isSuccessful) {
                    productId = product.id
                    product.location?.let { location ->
                        val geoFire = GeoFire(db.collection("ads"))
                        geoFire.setLocation(
                            productId,
                            GeoLocation(location.latitude, location.longitude)
                        )
                    }
                }
            }.await()

        return productId
    }

    suspend fun uploadImage(imageUri: Uri): String? {
        var productId: String? = null

        val storageRef = FirebaseStorage.getInstance().reference.child("images")
            .child("${UUID.randomUUID()}.${getFileExtension(imageUri.toString())}")

        storageRef.putFile(imageUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                storageRef.downloadUrl
            }.addOnCompleteListener { task ->
                productId = task.result.toString()

            }.await()

        return productId
    }

    suspend fun removeImages(imagesNeededToBeRemoved: List<String>) {
        val myList = imagesNeededToBeRemoved.toMutableList()
        myList.removeAll{ it.isEmpty()}

        withContext(Dispatchers.IO) {
            while (myList.isNotEmpty()) {
                if (deleteFile(myList.first())) myList.removeFirst()
            }
        }
    }

    private suspend fun deleteFile(fileUrl: String): Boolean {
        var isSuccessful = false
        FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl).delete()
            .addOnCompleteListener {
                isSuccessful = it.isSuccessful
            }.await()
        return isSuccessful
    }

    suspend fun updateImageLink(productId: String, imageLinks: Array<String>): Boolean {
        var isSuccessful = false
        db.document("ads/$productId")
            .update("imgUrls", FieldValue.arrayUnion(*imageLinks))
            .addOnCompleteListener {
                isSuccessful = it.isSuccessful
            }.await()

        return isSuccessful


    }

    fun productPager(keyword: String): Flow<PagingData<Product>> {
        val query = db.collection("ads").whereArrayContains("keywords", keyword)
            .whereNotEqualTo("sellerId", getThisUserUid())

        return Pager(
            PagingConfig(pageSize = ProductPagingSource.PAGE_SIZE)
        ) {
            ProductPagingSource(query)
        }.flow
    }

    suspend fun updateUser(user: User): Boolean {
        var isSuccessful: Boolean = false

        db.document("users/${user.id}")
            .set(user, SetOptions.merge()).addOnCompleteListener {
                isSuccessful = it.isSuccessful
            }.await()

        return isSuccessful
    }
}