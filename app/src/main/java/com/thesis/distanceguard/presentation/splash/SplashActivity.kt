package com.thesis.distanceguard.presentation.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.thesis.distanceguard.presentation.main.activity.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this,
            MainActivity::class.java)
        startActivity(intent)
    }
}