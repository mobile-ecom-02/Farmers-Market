package com.ilatyphi95.farmersmarket.notifications

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ilatyphi95.farmersmarket.R

const val UPLOAD_IMAGE_NOTIFICATION_CHANNEL_ID = "upload_image_notification_channel"
const val UPLOAD_ID = 300

class NotificationService {

    companion object {

        fun showNotification(context: Context, message: String, pendingIntent: PendingIntent? = null) {
            val builder = NotificationCompat.Builder(context, UPLOAD_IMAGE_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notify_icon)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)

            pendingIntent?.let {
                builder.setContentIntent(it)
            }

            with(NotificationManagerCompat.from(context)) {
                notify(UPLOAD_ID, builder.build())
            }
        }
    }
}