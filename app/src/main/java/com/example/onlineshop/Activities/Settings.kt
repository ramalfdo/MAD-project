package com.example.onlineshop.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.onlineshop.R
import com.example.onlineshop.firestore.FirestoreClass
import com.example.onlineshop.models.User
import com.example.onlineshop.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_settings.*

class Settings : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupActionbar()
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

    private fun getUserDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this)
    }
    fun userDetailsSuccess(user: User){
        hideProgressDialog()
        GlideLoader(this@Settings).loadUserPicture(user.image,iv_user_photo)
        tv_name.text="${user.firstName}${user.lastname}"
        tv_gender.text=user.gender
        tv_email.text=user.email
        tv_mobile_number.text="${user.mobile}"
    }

    override fun onResume(){
        super.onResume()
        getUserDetails()
    }

}