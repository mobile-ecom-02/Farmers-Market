package com.ilatyphi95.farmersmarket

import android.app.Application
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.jakewharton.threetenabp.AndroidThreeTen

class FarmersMarketApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        // initialize the time library
        AndroidThreeTen.init(this)
    }

    companion object {
        val requestOption by lazy {
            RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        }
    }
}