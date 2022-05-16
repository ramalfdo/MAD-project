package com.example.onlineshop.activity.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.onlineshop.Activities.Dashboard
import com.example.onlineshop.R
import com.example.onlineshop.firestore.FirestoreClass
import com.example.onlineshop.models.User
import com.example.onlineshop.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class Login : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        tv_forgot_password.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        tv_register.setOnClickListener(this)
        /*tv_register.setOnClickListener {
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)
        }*/
    }
    fun userLoggedInSuccess(user: User){
        hideProgressDialog()

        //we can use under code as login details
        /*Log.i("First Name: ",user.firstName)
        Log.i("Last Name: ",user.lastname)
        Log.i("Email: ",user.email) */

        if (user.profileCompleted==0){
            val intent=Intent(this@Login,UserProfile::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS,user)
            startActivity(intent)
        }


        else{
            startActivity(Intent(this@Login, Dashboard::class.java))
        }

        //startActivity(Intent(this@Login,Main::class.java))
        finish()
    }


    override fun onClick(View: View?){
        if(View !=null){
            when(View.id){
                R.id.tv_forgot_password->{
                    val intent=Intent(this@Login, ForgotPassword::class.java)
                    startActivity(intent)
                }
                R.id.btn_login->{
                    logInRegisteredUser()
                    //validateLoginDetails()

                }
                R.id.tv_register->{
                    val intent=Intent(this@Login, Register::class.java)
                    startActivity(intent)
                }
            }
        }
    }
    private fun validateLoginDetails():Boolean{
        return when{
            TextUtils.isEmpty(et_email.text.toString().trim{it<=' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim{it<=' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true)
                false
            }
            else->{
                //showErrorSnackBar("your details are valid.",false)
                true

            }
        }
    }
    private fun logInRegisteredUser(){
        if(validateLoginDetails()){
            showProgressDialog(resources.getString(R.string.please_wait))

            val email=et_email.text.toString().trim(){it<=' '}
            val password=et_password.text.toString().trim(){it<=' '}

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener{ task->
                    //hideProgressDialog()
                    if (task.isSuccessful){
                        FirestoreClass().getUserDetails(this@Login)
                        showErrorSnackBar("you are logged in successfully",false)
                    }
                    else{
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(),true)
                    }
                }
        }
    }
}