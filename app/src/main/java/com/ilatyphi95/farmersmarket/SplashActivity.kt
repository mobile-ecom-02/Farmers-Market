package com.ilatyphi95.farmersmarket

import android.app.ActivityOptions
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_splash.*
import android.util.Pair as UtilPair

class SplashActivity : AppCompatActivity() {

    private val broadcastReceiver = KillSplashActivityReceiver(this)


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.SplashTheme)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val filter = IntentFilter("com.action.killSplash")
        registerReceiver(broadcastReceiver, filter)

        goToNextActivity()

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun goToNextActivity(){
        Handler().postDelayed(
            Runnable
            {
                val intent = Intent(this, LoginActivity::class.java)
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    this,
                    UtilPair.create(root, "headerContainer"),
                    UtilPair.create(treeImage, "treeImage")
                )
                startActivity(intent, options.toBundle())
//                finish()
            }, 1500
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
}