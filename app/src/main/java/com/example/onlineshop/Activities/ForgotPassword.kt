package com.example.onlineshop.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.onlineshop.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_register.*

class ForgotPassword : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        btn_submit.setOnClickListener{
            val email:String=et_email_forgot_password.text.toString().trim{it<=' '}
            if (email.isEmpty()){
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
            }
            else{
                showProgressDialog(resources.getString(R.string.please_wait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener{task ->
                hideProgressDialog()
                        if (task.isSuccessful){
                            Toast.makeText(this@ForgotPassword,resources.getString(R.string.email_sent_success),Toast.LENGTH_LONG)
                                .show()
                            finish()
                        }else{
                            showErrorSnackBar(task.exception!!.message.toString(),true)
                        }
                    }
            }

        }
    }
}