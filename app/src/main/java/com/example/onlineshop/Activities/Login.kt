package com.example.onlineshop.Activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.onlineshop.R
import com.google.firebase.auth.FirebaseAuth
import io.grpc.InternalChannelz.id
import kotlinx.android.synthetic.main.activity_login.*

class Login : BaseActivity(),View.OnClickListener {
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

    override fun onClick(View:View?){
        if(View !=null){
            when(View.id){
                R.id.tv_forgot_password->{
                    val intent=Intent(this@Login,ForgotPassword::class.java)
                    startActivity(intent)
                }
                R.id.btn_login->{
                    logInRegisteredUser()

                }
                R.id.tv_register->{
                    val intent=Intent(this@Login,Register::class.java)
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
                    hideProgressDialog()
                    if (task.isSuccessful){
                        showErrorSnackBar("you are logged in successfully",false)
                    }
                    else{
                        showErrorSnackBar(task.exception!!.message.toString(),true)
                    }
                }
        }
    }
}
