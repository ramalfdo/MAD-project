package com.example.onlineshop.activity.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.onlineshop.R
import com.example.onlineshop.firestore.FirestoreClass
import com.example.onlineshop.models.Product
import com.example.onlineshop.utils.Constants
import com.example.onlineshop.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_add_product.*
import java.io.IOException

class AddProduct : BaseActivity(), View.OnClickListener {

    private var mSelectedImageFileURI: Uri? = null
    private var mProductImageURL:String= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        setupActionbar()

        iv_add_update_product.setOnClickListener(this)
        btn_submit_add_product.setOnClickListener(this)
    }
    private fun setupActionbar(){
        setSupportActionBar(toolbar_add_product)
        val actionBar=supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_24dp)
        }
        toolbar_add_product.setNavigationOnClickListener{onBackPressed()}
    }

    override fun onClick(p0: View?) {
        if (p0 !=null){
            when(p0.id){
                R.id.iv_add_update_product ->{
                    if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
                    {
                        Constants.showImageChooser(this@AddProduct)
                    }
                    else{
                        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        Constants.READ_STORAGE_PERMISSION_CODE)
                    }

                }

                R.id.btn_submit_add_product->{
                    if (validateProductDetails()){
                        //showErrorSnackBar("your details validate",false)
                        uploadProductImage()
                    }
                }
            }
        }
    }
    private fun uploadProductImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().uploadImageToCloudStorage(this,mSelectedImageFileURI, Constants.PRODUCT_IMAGE)
    }
    fun productUploadSuccess(){
        hideProgressDialog()
        Toast.makeText(this@AddProduct,resources.getString(R.string.product_upload_success_message),
        Toast.LENGTH_SHORT).show()
        finish()
    }
    fun imageUploadSuccess(imageURL: String){
        //hideProgressDialog()
        //showErrorSnackBar("product upload.Image URL:$imageURL",false)
        mProductImageURL = imageURL
        uploadProductDetails()
        /*Toast.makeText(
            this@UserProfile,"your image is uploaded successfully.image URL is $imageURL",
            Toast.LENGTH_SHORT
        ).show()*/
    }
    private fun uploadProductDetails(){
        val username = this.getSharedPreferences(Constants.ONLINESHOP_PREFERENCES, Context.MODE_PRIVATE)
            .getString(Constants.LOGGED_IN_USERNAME,"")!!

        val product = Product(FirestoreClass().getCurrentUserID(),
            username,
            et_product_title.text.toString().trim{ it <= ' '},
            et_product_price.text.toString().trim{ it <= ' '},
            et_product_description.text.toString().trim{ it <= ' '},
            et_product_quantity.text.toString().trim{ it <= ' '},
            mProductImageURL
        )
        FirestoreClass().uploadProductDetails(this,product)
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
                    iv_add_update_product.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_eddit_pencil))

                    mSelectedImageFileURI =data.data!!
                    try{
                        GlideLoader(this).loadUserPicture(mSelectedImageFileURI!!, iv_product_image)
                    }
                    catch (e:IOException){
                        e.printStackTrace()

                    }
                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED){
            Log.e("Request Cancelled","Image selection cancelled")
        }
    }
    private fun validateProductDetails():Boolean{
        return when{
            mSelectedImageFileURI == null->{
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image),true)
                false
            }
            TextUtils.isEmpty(et_product_title.text.toString().trim{it <=' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title),true)
                false
            }
            TextUtils.isEmpty(et_product_price.text.toString().trim{it <=' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price),true)
                false
            }
            TextUtils.isEmpty(et_product_description.text.toString().trim{it <=' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_description),true)
                false
            }
            TextUtils.isEmpty(et_product_quantity.text.toString().trim{it <=' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_quantity),true)
                false
        }
            else->{
                true

            }
        }
    }
}