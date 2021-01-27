package com.ilatyphi95.farmersmarket.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ilatyphi95.farmersmarket.firebase.services.ProductServices
import com.ilatyphi95.farmersmarket.ui.modifyad.KEY_IMAGE_URL
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DeleteImageWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        var fileUrl = ""
        return try {
            fileUrl = inputData.getString(KEY_IMAGE_URL)!!
            if(ProductServices.deleteFile(fileUrl)) {
                Result.success()
            } else {
                Result.failure()
            }
        } catch (throwable: Throwable) {
            Log.e("DeleteImageWorker: $fileUrl", "doWork: Error", throwable)
            Result.failure()
        }
    }
}