package com.ilatyphi95.farmersmarket

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.lang.NullPointerException

class FirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FirebaseMessagingServ"
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

        var notificationBody = ""
        var notificationTitle = ""
        var notificationData = ""

        try {
            notificationData = remoteMessage.data.toString()
            notificationTitle = remoteMessage.notification?.title.toString()
            notificationBody = remoteMessage.notification?.body.toString()

            

        }catch (e: NullPointerException){
            Log.d(TAG, "onMessageReceived: NullPointerException " + e.message);
        }
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    fun sendRegistrationToServer(token : String){
        Log.d(TAG, "sendRegistrationToServer: sending token to server " + token)
    }

}