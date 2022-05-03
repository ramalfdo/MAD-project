package com.example.onlineshop.activity.firestore
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.media.audiofx.BassBoost
import android.net.Uri
import android.util.Log
import com.example.onlineshop.activity.activity.Login
import com.example.onlineshop.activity.activity.Register
import com.example.onlineshop.activity.activity.Settings
import com.example.onlineshop.activity.activity.UserProfile
import com.example.onlineshop.activity.models.User
import com.example.onlineshop.activity.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass {
    private val mFirestore=FirebaseFirestore.getInstance()

    fun registerUser(activity: Register, userInfo: User){
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
        val currentUser= FirebaseAuth.getInstance().currentUser
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
                    is Login ->{
                        activity.userLoggedInSuccess(user)
                    }
                    is Settings ->{

                        activity.userDetailsSuccess(user)
                    }
                }

            }
            .addOnFailureListener{e->
                when(activity){//in here i change UserProfile as Login
                    is Login -> {
                        activity.hideProgressDialog()
                    }
                    is Settings->{
                        activity.hideProgressDialog()
                    }
                    //  is BassBoost.Settings ->{
                    //     activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName,"error while user details.",e
                )
            }
    }

    fun updateUserProfileData(activity: Activity,userHashMap: HashMap<String, Any>) {

        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when(activity){//in here i change UserProfile as Login
                    is UserProfile -> {
                        activity.userProfileUpdateSuccess()
                    }
                    //  is BassBoost.Settings ->{
                    //     activity.hideProgressDialog()
                }
            }
            .addOnFailureListener{e->
                when(activity){//in here i change UserProfile as Login
                    is UserProfile -> {
                        activity.hideProgressDialog()
                    }
                    //  is BassBoost.Settings ->{
                    //     activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName,"error while user details.",e
                )
            }
    }

    fun uploadImageToCloudStorage(activity:Activity,imageFileURI: Uri?){

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "." + Constants.getFileExtension(activity,imageFileURI
            )
        )
        sRef.putFile(imageFileURI!!).addOnSuccessListener {
                taskSnapshot->
            Log.e("Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri -> Log.e("Downloadable Image URL",uri.toString())

                    when(activity){
                        is UserProfile->{
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }
        }
            .addOnFailureListener{ //hide the progress bar and print the error in log.
                    exception->
                when(activity){
                    is UserProfile->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }
}