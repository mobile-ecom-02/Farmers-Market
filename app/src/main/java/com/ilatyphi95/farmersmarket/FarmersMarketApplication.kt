package com.ilatyphi95.farmersmarket

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ilatyphi95.farmersmarket.utils.NightMode
import com.jakewharton.threetenabp.AndroidThreeTen
import java.util.*

class FarmersMarketApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        // initialize the time library
        AndroidThreeTen.init(this)
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        preferences.getString(
            getString(R.string.pref_key_night),
            getString(R.string.pref_night_auto)
        )?.apply {
            val mode = NightMode.valueOf(this.toUpperCase(Locale.US))
            AppCompatDelegate.setDefaultNightMode(mode.value)
        }
    }

    companion object {
        val requestOption by lazy {
            RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        }
    }
}