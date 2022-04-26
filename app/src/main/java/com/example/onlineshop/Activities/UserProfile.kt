package com.example.onlineshop.Activities

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.onlineshop.R
import com.example.onlineshop.models.User
import com.example.onlineshop.utils.Constants
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException
import java.util.jar.Manifest

class UserProfile : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        var userDetails: User =User()
        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            userDetails=intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        et_first_name.isEnabled=false
        et_first_name.setText(userDetails.firstName)

        et_last_name.isEnabled=false
        et_last_name.setText(userDetails.lastname)

        et_email.isEnabled=false
        et_email.setText(userDetails.email)

        iv_user_photo.setOnClickListener(this@UserProfile)
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
            }
        }
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
                        val selectedImageFileUri = data.data!! //image select from the phone storage
                        iv_user_photo.setImageURI(Uri.parse(selectedImageFileUri.toString()))
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
}