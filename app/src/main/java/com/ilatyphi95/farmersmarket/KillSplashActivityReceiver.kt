package com.ilatyphi95.farmersmarket

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class KillSplashActivityReceiver(private val activity: Activity) : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        activity.finish()
    }
}