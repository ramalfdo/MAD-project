package com.example.onlineshop.firestore

import android.media.session.MediaSessionManager
import android.util.Log
import com.example.onlineshop.Activities.BaseActivity
import com.example.onlineshop.Activities.Register
import com.example.onlineshop.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firestore.v1.FirestoreGrpc

class FirestoreClass {
    private val mFirestore=FirebaseFirestore.getInstance()

    fun registerUser(activity: Register,userInfo: User){
        mFirestore.collection("users")//collection name
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
}