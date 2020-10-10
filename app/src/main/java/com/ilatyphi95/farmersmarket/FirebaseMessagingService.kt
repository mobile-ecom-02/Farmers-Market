package com.ilatyphi95.farmersmarket

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.lang.NullPointerException

class FirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FirebaseMessagingServ";
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        var notificationBody = "";
        var notificationTitle = "";
        var notificationData = "";

        try {
            notificationData = p0.data.toString();
            

        }catch (e: NullPointerException){
            Log.d(TAG, "onMessageReceived: NullPointerException " + e.message);
        }
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

}