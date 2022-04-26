package com.example.onlineshop.Activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineshop.R
import com.example.onlineshop.utils.Constants
import com.google.protobuf.Value
import kotlinx.android.synthetic.main.main.*

class Main : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val sharedPreferences=getSharedPreferences(Constants.ONLINESHOP_PREFERENCES, MODE_PRIVATE)//get data package from
        val username=sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!
        tv_main.text="hello $username."

    }
}