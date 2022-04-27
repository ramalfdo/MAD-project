package com.example.onlineshop.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.onlineshop.R
import kotlinx.android.synthetic.main.activity_settings.*

class Settings : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }
    private fun setupActionbar(){
        setSupportActionBar(toolbar_settings_activity)
        val actionBar=supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_24dp)
        }
        toolbar_settings_activity.setNavigationOnClickListener{onBackPressed()}
    }
}