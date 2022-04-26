package com.example.onlineshop.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.media.session.MediaSessionManager
import android.util.Log
import com.example.onlineshop.Activities.BaseActivity
import com.example.onlineshop.Activities.Login
import com.example.onlineshop.Activities.Register
import com.example.onlineshop.models.User
import com.example.onlineshop.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firestore.v1.FirestoreGrpc

class FirestoreClass {
    private val mFirestore=FirebaseFirestore.getInstance()

    fun registerUser(activity: Register,userInfo: User){
        mFirestore.collection(Constants.USERS)//collection name
            .document(userInfo.id)//document for user id
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener{
                e-> activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while register the user.",e
                )
            }

    }
    fun getCurrentUserID():String{
        val currentUser=FirebaseAuth.getInstance().currentUser
        var currentUserID=""
        if(currentUser!=null){
            currentUserID=currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity){
        mFirestore.collection((Constants.USERS))
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document->
                Log.i(activity.javaClass.simpleName,document.toString())
                val user=document.toObject(User::class.java)!!

                val sharedPreferences=
                    activity.getSharedPreferences(
                        Constants.ONLINESHOP_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor=sharedPreferences.edit()
                //key for logged in user name
                //value
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName}${user.lastname}"
                )
                editor.apply()
                //pass results to the login activity
                when(activity){
                    is Login->{
                        activity.userLoggedInSuccess(user)
                    }
                }
            }
    }
}