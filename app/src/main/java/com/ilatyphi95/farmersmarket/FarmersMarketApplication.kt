package com.ilatyphi95.farmersmarket

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class FarmersMarketApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        // initialize the time library
        AndroidThreeTen.init(this)
    }
}