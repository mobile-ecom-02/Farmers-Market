package com.ilatyphi95.farmersmarket.ui.modifyad

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ilatyphi95.farmersmarket.HomeActivity
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.firebase.services.ProductServices
import com.ilatyphi95.farmersmarket.notifications.NotificationService
import kotlinx.coroutines.ExperimentalCoroutinesApi

const val KEY_PRODUCT_ID = "product-id"
@ExperimentalCoroutinesApi
class UpdateImageLinkWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    private val tag = "UpdateImageLinkWorker"
    private val appContext = applicationContext

    override suspend fun doWork(): Result {
        return try {

            val productId = inputData.getString(KEY_PRODUCT_ID)!!
            val imageLinks = inputData.getStringArray(KEY_OUTPUT_URL_LIST)!!

            if(ProductServices.updateImageLink(productId, imageLinks)) {

                NotificationService
                    .showNotification(appContext,
                        appContext.getString(R.string.upload_complete), getPendingIntent())
                Result.success()
            } else {
                Result.failure()
            }

        } catch (throwable: Throwable) {
            Log.e(tag, "doWork: Update Link", throwable )
            Result.failure()
        }
    }

    private fun getPendingIntent() : PendingIntent {
        return NavDeepLinkBuilder(appContext)
            .setComponentName(HomeActivity::class.java)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.navigation_pager)
            .createPendingIntent()
    }
}