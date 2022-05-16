package com.example.onlineshop.activity.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineshop.R
import com.example.onlineshop.adapters.CartItemListAdapter
import com.example.onlineshop.firestore.FirestoreClass
import com.example.onlineshop.models.CartItem
import com.example.onlineshop.models.Product
import com.example.onlineshop.utils.Constants
import kotlinx.android.synthetic.main.activity_cart_list.*

class CartList : BaseActivity() {

    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartListItems: ArrayList<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)
        setupActionBar()

        btn_checkout.setOnClickListener {
            val intent = Intent(this@CartList, AddressList::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
        }
    }
    fun successCartItemList(cartList: ArrayList<CartItem>){
        hideProgressDialog()

        for (product in mProductList){
            for (cartItem in cartList){
                if (product.product_id == cartItem.product_id){
                    cartItem.stock_quantity = product.stock_quantity
                    if (product.stock_quantity.toInt() == 0){
                        cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        mCartListItems = cartList

        if (mCartListItems.size > 0){
            rv_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE
            tv_no_cart_item_found.visibility = View.GONE

            rv_cart_items_list.layoutManager = LinearLayoutManager(this@CartList)
            rv_cart_items_list.setHasFixedSize(true)
            val cartListAdapter = CartItemListAdapter(this@CartList, mCartListItems, true)
            rv_cart_items_list.adapter = cartListAdapter
            var subTotal: Double = 0.0
            for (item in mCartListItems){

                val availableQuantity = item.stock_quantity.toInt()
                if (availableQuantity > 0){
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    subTotal += (price * quantity)
                }
            }
            tv_sub_total.text = "Rs: $subTotal"
            tv_shipping_charge.text = "Rs:10.0"
            if (subTotal > 0){
                ll_checkout.visibility = View.VISIBLE
                val total = subTotal + 10
                tv_total_amount.text = "Rs:$total"
            }
            else{
                ll_checkout.visibility = View.GONE
            }
        }
        else{
            rv_cart_items_list.visibility = View.GONE
            ll_checkout.visibility = View.GONE
            tv_no_cart_item_found.visibility = View.VISIBLE
        }
    }

    fun successProductListFromFireStore(productsList: ArrayList<Product>){
        hideProgressDialog()
        mProductList = productsList
        getCartItemsList()

    }

    private fun getProductList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductsList(this)
    }

    private fun getCartItemsList(){
        //showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getCartList(this@CartList)
    }

    fun itemUpdateSuccess(){
        hideProgressDialog()
        getCartItemsList()
    }

    override fun onResume() {
        super.onResume()
        //getCartItemsList()
        getProductList()
    }
     fun itemRemovedSuccess(){
         hideProgressDialog()
         Toast.makeText(this@CartList,
         resources.getString(R.string.msg_item_removed_successfully),
         Toast.LENGTH_SHORT).show()

         getCartItemsList()
     }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_cart_list_activity)
        val actionbar = supportActionBar
        if(actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_back_24dp)
        }
        toolbar_cart_list_activity.setNavigationOnClickListener{onBackPressed()}

    }
}