package com.example.onlineshop.Activities

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.onlineshop.R
import com.example.onlineshop.firestore.FirestoreClass
import com.example.onlineshop.models.User
import com.example.onlineshop.utils.Constants
import com.example.onlineshop.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException
import java.util.jar.Manifest

class UserProfile : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User

    private var mSelectedImageFileUri: Uri? =  null
    private var mUserProfileImageURL:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            mUserDetails=intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        et_first_name.isEnabled=false
        et_first_name.setText(mUserDetails.firstName)

        et_last_name.isEnabled=false
        et_last_name.setText(mUserDetails.lastname)

        et_email.isEnabled=false
        et_email.setText(mUserDetails.email)

        iv_user_photo.setOnClickListener(this@UserProfile)

        btn_submit.setOnClickListener(this@UserProfile)
    }

    override fun onClick(View: View?) {
        if(View != null){
            when(View.id){
                R.id.iv_user_photo -> {
                    if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    {
                        //showErrorSnackBar("you already have the storage permission.",false)
                        Constants.showImageChooser(this)

                    }
                    else{ ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
                    }
                }

                R.id.btn_submit -> {


                    if (validateUserProfileDetails()){

                        showProgressDialog(resources.getString(R.string.please_wait))

                        if(mSelectedImageFileUri != null)

                            FirestoreClass().uploadImageToCloudStorage(this,mSelectedImageFileUri)

                        else{
                            updateUserProfileDetails()
                        }
                        //showErrorSnackBar("your details are valid. you can update them.",false)
                    }
                }
            }
        }
    }
    private fun updateUserProfileDetails(){
        val userHashMap = HashMap<String,Any>()
        val mobileNumber = et_mobile_number.text.toString().trim{ it <= ' '}

        val gender = if (rb_male.isChecked){
            Constants.MALE
        }else{
            Constants.FEMALE
        }

        if(mUserProfileImageURL.isNotEmpty()){
            userHashMap[Constants.IMAGE]=mUserProfileImageURL
        }
        if(mobileNumber.isNotEmpty()){
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }
        userHashMap[Constants.GENDER] = gender

        //showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().updateUserProfileData(this,userHashMap)
    }
    fun userProfileUpdateSuccess(){
        hideProgressDialog()
        Toast.makeText(this@UserProfile,resources.getString(R.string.msg_profile_update_success),
        Toast.LENGTH_SHORT).show()

        startActivity(Intent(this@UserProfile,Main::class.java))
        finish()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty()&&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //showErrorSnackBar("The storage permission is granted.",false)
                Constants.showImageChooser(this)

            }
            else{
                Toast.makeText(this,resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE){
                if (data != null){
                    try {
                        mSelectedImageFileUri = data.data!! //image select from the phone storage
                        //iv_user_photo.setImageURI(Uri.parse(selectedImageFileUri.toString()))

                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, iv_user_photo)
                    }
                    catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this@UserProfile,resources.getString(R.string.image_selection_failed),
                        Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED){
            Log.e("Request Cancelled","Image selection cancelled")
        }
    }
    private fun validateUserProfileDetails():Boolean{
        return when{
            TextUtils.isEmpty(et_mobile_number.text.toString().trim{ it <= ' '}) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number),true)
                false
            }
            else ->{
                true
            }
        }
    }
    fun imageUploadSuccess(imageURL: String){
        //hideProgressDialog()
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
        /*Toast.makeText(
            this@UserProfile,"your image is uploaded successfully.image URL is $imageURL",
            Toast.LENGTH_SHORT
        ).show()*/
    }
}