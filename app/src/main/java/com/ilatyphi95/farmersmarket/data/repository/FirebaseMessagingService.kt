package com.ilatyphi95.farmersmarket.data.repository

import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ilatyphi95.farmersmarket.HomeActivity
import com.ilatyphi95.farmersmarket.R

const val MESSAGE_NOTIFICATION_CHANNEL_ID = "message_notification_channel"
class FirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FirebaseMessagingServ"

        fun useToken(execute: (String) -> Unit) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if(!task.isSuccessful) {
                    Log.d(TAG, "useToken: ${task.exception?.message}")
                }

                task.result?.let {
                    execute(it)
                }
            }
        }

        fun sendRegistrationToServer(token : String){
            val user = FirebaseAuth.getInstance().currentUser
            user?.let {
                val map = HashMap<String, FieldValue>()
                map["deviceTokens"] = FieldValue.arrayUnion(token)
                FirebaseFirestore.getInstance().document("users/${it.uid}").set(
                    map, SetOptions.merge())
            }
            Log.d(TAG, "sendRegistrationToServer: sending token to server " + token)
        }

        fun removeRegistrationFromServer(token : String){
            val user = FirebaseAuth.getInstance().currentUser
            user?.let {
                val map = HashMap<String, FieldValue>()
                map["deviceTokens"] = FieldValue.arrayRemove(token)
                FirebaseFirestore.getInstance().document("users/${it.uid}").set(
                    map, SetOptions.merge())
            }
            Log.d(TAG, "sendRegistrationToServer: sending token to server " + token)
        }

    }

    /**
     * Called when FCM registration is updated
     */

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken: $token")

        sendRegistrationToServer(token)
    }

    /**
     * Called when message is received
     *
     * @param remoteMessage object represents the message received from Firebase Cloud Messaging
     */

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val notificationBody: String
        val notificationTitle: String

        try {
            val notificationData = remoteMessage.data
            notificationTitle = remoteMessage.notification?.title.toString()
            notificationBody = remoteMessage.notification?.body.toString()
            val messageId = notificationData["messageId"]

            val pendingIntent = NavDeepLinkBuilder(this)
                .setComponentName(HomeActivity::class.java)
                .setGraph(R.navigation.mobile_navigation)
                .setDestination(R.id.chatFragment)
                .setArguments(
                    Bundle().apply {
                        putString("messageId", messageId)
                    }
                )
                .createPendingIntent()

            val builder = NotificationCompat.Builder(this, MESSAGE_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notify_icon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(this)) {
                notify(1, builder.build())
            }

        } catch (e: NullPointerException){
            Log.d(TAG, "onMessageReceived: NullPointerException " + e.message)
        }
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }
}