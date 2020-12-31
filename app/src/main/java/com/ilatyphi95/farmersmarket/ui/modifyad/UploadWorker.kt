package com.ilatyphi95.farmersmarket.ui.modifyad

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.ilatyphi95.farmersmarket.firebase.services.ProductServices
import kotlinx.coroutines.ExperimentalCoroutinesApi

const val KEY_IMAGE_URL = "key-image-url"
const val KEY_OUTPUT_URL_LIST = "key-output-url-list"
@ExperimentalCoroutinesApi
class UploadWorker(ctx: Context, params: WorkerParameters) :
    CoroutineWorker(ctx, params){
    override suspend fun doWork(): Result {
//        val appContext = applicationContext

        return try {
            val imageUrlString = inputData.getString(KEY_IMAGE_URL)
            val outputUrlList = inputData.getStringArray(KEY_OUTPUT_URL_LIST)!!.toMutableList()


            if(TextUtils.isEmpty(imageUrlString)) {
                Log.e("UploadWorker", "doWork: Invalid input" )
                throw IllegalArgumentException("Invalid input")
            }

            val uploadUrl: String? = ProductServices.uploadImage(Uri.parse((imageUrlString)))

            return if(uploadUrl == null) {
                Result.failure()
            } else {
                outputUrlList.add(uploadUrl)
                val outputData = workDataOf(KEY_OUTPUT_URL_LIST to outputUrlList.toTypedArray())

                Result.success(outputData)
            }

        } catch (throwable: Throwable) {
            Log.e("UploadWorker", "doWork: Error", throwable)
            Result.failure()
        }
    }
}