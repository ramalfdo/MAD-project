package com.example.onlineshop.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineshop.R


class SplashScreenActivity : AppCompatActivity() {

    private val SPLASH_TIME:Long=3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed( {
            startActivity(Intent(this, Dashboard::class.java))
            finish()
        }, SPLASH_TIME)

    }

}