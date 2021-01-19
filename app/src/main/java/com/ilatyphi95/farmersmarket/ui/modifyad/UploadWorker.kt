package com.ilatyphi95.farmersmarket.ui.modifyad

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.firebase.services.ProductServices
import com.ilatyphi95.farmersmarket.notifications.NotificationService
import com.ilatyphi95.farmersmarket.notifications.UPLOAD_IMAGE_NOTIFICATION_CHANNEL_ID
import kotlinx.coroutines.ExperimentalCoroutinesApi

const val KEY_IMAGE_URL = "key-image-url"
const val KEY_OUTPUT_URL_LIST = "key-output-url-list"
const val KEY_TOTAL_IMAGE = "key-total-image"
@ExperimentalCoroutinesApi
class UploadWorker(ctx: Context, params: WorkerParameters) :
    CoroutineWorker(ctx, params){
    private val appContext = applicationContext

    override suspend fun doWork(): Result {

        createNotificationChannel()

        return try {
            val imageUrlString = inputData.getString(KEY_IMAGE_URL)
            val outputUrlList = inputData.getStringArray(KEY_OUTPUT_URL_LIST)!!.toMutableList()
            val currentImage = outputUrlList.size + 1
            val totalImage = inputData.getInt(KEY_TOTAL_IMAGE, 1)
            val message = appContext.getString(R.string.uploading_image, currentImage, totalImage)

            NotificationService.showNotification(appContext, message)

            if(TextUtils.isEmpty(imageUrlString)) {
                Log.e("UploadWorker", "doWork: Invalid input" )
                throw IllegalArgumentException("Invalid input")
            }

            val uploadUrl: String? = ProductServices.uploadImage(Uri.parse((imageUrlString)))

            return if(uploadUrl == null) {
                Result.failure()
            } else {
                outputUrlList.add(uploadUrl)
                val outputData = workDataOf(
                    KEY_OUTPUT_URL_LIST to outputUrlList.toTypedArray(),
                    KEY_TOTAL_IMAGE to totalImage
                )

                Result.success(outputData)
            }

        } catch (throwable: Throwable) {
            Log.e("UploadWorker", "doWork: Error", throwable)
            Result.failure()
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = appContext.getString(R.string.upload_channel_name)
            val descriptionText = appContext.getString(R.string.upload_channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(UPLOAD_IMAGE_NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}