package com.example.onlineshop.activity.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.onlineshop.R
import com.example.onlineshop.firestore.FirestoreClass
import com.example.onlineshop.models.CartItem
import com.example.onlineshop.models.Product
import com.example.onlineshop.utils.Constants
import com.example.onlineshop.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_product_details.*

class ProductDetails : BaseActivity(), View.OnClickListener {

    private var mProductId:String = ""
    private  lateinit var mProductDetails: Product
    private var mProductOwnerId : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)){
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            //Log.i("Product Id",mProductId)
        }
        //var productOwnerId : String = ""
        if(intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            mProductOwnerId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
            }

        if (FirestoreClass().getCurrentUserID() == mProductOwnerId){
            btn_add_to_cart.visibility = android.view.View.GONE
            btn_go_to_cart.visibility = android.view.View.GONE
            }
        else{
            btn_add_to_cart.visibility = View.VISIBLE
        }

        getProductDetails()
        btn_add_to_cart.setOnClickListener(this)
        btn_go_to_cart.setOnClickListener(this)
    }

    private fun getProductDetails() {

        // Show the product dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of FirestoreClass to get the product details.
        FirestoreClass().getProductDetails(this, mProductId)
    }

    fun productExistsInCart(){
        hideProgressDialog()
        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }



    fun productDetailsSuccess(product: Product){
        mProductDetails = product
       // hideProgressDialog()
        GlideLoader(this@ProductDetails).loadProductPicture(
            product.image,iv_product_detail_image
        )
        tv_product_details_title.text = product.title
        tv_product_details_price.text = "Rs:${product.price}"
        tv_product_details_description.text = product.description
        tv_product_details_stock_quantity.text = product.stock_quantity

        if (product.stock_quantity.toInt() == 0){
            hideProgressDialog()
            btn_add_to_cart.visibility = View.GONE
            tv_product_details_stock_quantity.text = resources.getString(R.string.lbl_out_of_stock)
            tv_product_details_stock_quantity.setTextColor(ContextCompat.getColor(this@ProductDetails,R.color.colorSnackBarError))
        }
        else{
            if (FirestoreClass().getCurrentUserID() == product.user_id){
                hideProgressDialog()
            }
            else{
                FirestoreClass().checkIfItemExistInCart(this, mProductId)
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_product_details_activity)
        val actionbar = supportActionBar
        if(actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_back_24dp)
        }
        toolbar_product_details_activity.setNavigationOnClickListener{onBackPressed()}

    }

    private fun addToCart(){
        val cartItem = CartItem(
            FirestoreClass().getCurrentUserID(),
            mProductOwnerId,
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addCartItems(this, cartItem)
    }
    fun addToCartSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this@ProductDetails,resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT
        ).show()

        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }

    override fun onClick(p0: View?) {
        if(p0!=null){
            when(p0.id){
                R.id.btn_add_to_cart ->{
                    addToCart()
                }
                R.id.btn_go_to_cart ->{
                    startActivity(Intent(this@ProductDetails, CartList::class.java))
                }
            }
        }
    }


}