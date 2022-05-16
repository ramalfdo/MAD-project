package com.example.onlineshop.activity.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineshop.Activities.Dashboard
import com.example.onlineshop.R
import com.example.onlineshop.adapters.CartItemListAdapter
import com.example.onlineshop.firestore.FirestoreClass
import com.example.onlineshop.models.Address
import com.example.onlineshop.models.CartItem
import com.example.onlineshop.models.Order
import com.example.onlineshop.models.Product
import com.example.onlineshop.utils.Constants
import kotlinx.android.synthetic.main.activity_checkout.*

class Checkout : BaseActivity() {

    private var mAddressDetails: Address? = null
    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartItemsList: ArrayList<CartItem>
    private var mSubTotal: Double = 0.0
    private var mTotalAmount: Double= 0.0
    private lateinit var mOrderDetails: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails = intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)!!
        }

        if (mAddressDetails != null) {
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            tv_checkout_additional_note.text = mAddressDetails?.additionalNote

            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                tv_checkout_other_details.text = mAddressDetails?.otherDetails
            }
            tv_checkout_mobile_number.text = mAddressDetails?.mobileNumber
        }
        getProductList()

        btn_place_order.setOnClickListener{
            placeAnOrder()
        }
    }

    fun allDetailsUpdatedSuccessfully(){
        hideProgressDialog()

        Toast.makeText(this@Checkout, "Your order placed successfully.", Toast.LENGTH_SHORT)
            .show()

        val intent = Intent(this@Checkout, Dashboard::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun orderPlacedSuccess(){
        FirestoreClass().updateAllDetails(this, mCartItemsList, mOrderDetails)

    }
    fun successProductsListFromFireStore(productsList:ArrayList<Product>){
        mProductList = productsList
        getCartItemList()
    }
    private fun getCartItemList(){
        FirestoreClass().getCartList(this@Checkout)
    }

    private fun placeAnOrder(){
        showProgressDialog(resources.getString(R.string.please_wait))
// START
        if (mAddressDetails != null)
        {
            mOrderDetails = Order(
                FirestoreClass().getCurrentUserID(),
                mCartItemsList,
                mAddressDetails!!,
                "My order ${System.currentTimeMillis()}",
                mCartItemsList[0].image,
                mSubTotal.toString(),
                "10.0", //  Charge is fixed as $10 for now in our case.
                mTotalAmount.toString(),
                System.currentTimeMillis()
            )
            FirestoreClass().placeOrder(this@Checkout, mOrderDetails)
        }
    }
    fun successCartItemsList(cartList: ArrayList<CartItem>){
        hideProgressDialog()

        for (product in mProductList) {
            for (cart in cartList) {
                if (product.product_id == cart.product_id) {
                    cart.stock_quantity = product.stock_quantity
                }
            }
        }
        mCartItemsList = cartList

        rv_cart_list_items.layoutManager = LinearLayoutManager(this@Checkout)
        rv_cart_list_items.setHasFixedSize(true)

        val cartListAdapter = CartItemListAdapter(this@Checkout, mCartItemsList, false)
        rv_cart_list_items.adapter = cartListAdapter

        for (item in mCartItemsList){
            val availableQuantity = item.stock_quantity.toInt()
            if (availableQuantity > 0){
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                mSubTotal += (price * quantity)
            }
        }
        tv_checkout_sub_total.text = "Rs:$mSubTotal"
        tv_checkout_shipping_charge.text = "Rs:10.0"

        if (mSubTotal > 0) {
            ll_checkout_place_order.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 10.0
            tv_checkout_total_amount.text = "Rs:$mTotalAmount"
        } else {
            ll_checkout_place_order.visibility = View.GONE
        }
    }

    private fun getProductList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllProductsList(this@Checkout)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_checkout_activity)
        val actionbar = supportActionBar
        if(actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_back_24dp)
        }
        toolbar_checkout_activity.setNavigationOnClickListener{onBackPressed()}

    }
}