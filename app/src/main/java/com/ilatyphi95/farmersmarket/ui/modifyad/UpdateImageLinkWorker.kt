package com.ilatyphi95.farmersmarket.ui.modifyad

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ilatyphi95.farmersmarket.firebase.services.ProductServices
import kotlinx.coroutines.ExperimentalCoroutinesApi

const val KEY_PRODUCT_ID = "product-id"
@ExperimentalCoroutinesApi
class UpdateImageLinkWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    private val tag = "UpdateImageLinkWorker"

    override suspend fun doWork(): Result {
        return try {

            val productId = inputData.getString(KEY_PRODUCT_ID)!!
            val imageLinks = inputData.getStringArray(KEY_OUTPUT_URL_LIST)!!

            if(ProductServices.updateImageLink(productId, imageLinks)) {
                Result.success()
            } else {
                Result.failure()
            }

        } catch (throwable: Throwable) {
            Log.e(tag, "doWork: Update Link", throwable )
            Result.failure()
        }
    }
}