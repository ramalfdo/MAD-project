package com.example.onlineshop.activity.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineshop.R
import com.example.onlineshop.adapters.AddressListAdapter
import com.example.onlineshop.firestore.FirestoreClass
import com.example.onlineshop.models.Address
import com.example.onlineshop.utils.Constants
import com.example.onlineshop.utils.SwipeToDeleteCallback
import com.example.onlineshop.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_address_list.*

class AddressList : BaseActivity() {

    private var mSelectAddress:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)
        setupActionbar()
  //    getAddressList()

        tv_add_address.setOnClickListener{
            val intent = Intent(this@AddressList,AddEditAddress::class.java)
            startActivity(intent)
         //   startActivityForResult(intent,Constants.ADD_ADDRESS_REQUEST_CODE)
         startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        }
       getAddressList()
        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)){
            mSelectAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS,false)
        }
        if (mSelectAddress){
            tv_title_address_list.text = resources.getString(R.string.title_select_address)
        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
         if (requestCode == Constants.ADD_ADDRESS_REQUEST_CODE)
            getAddressList()
        }
        else if (resultCode == Activity.RESULT_CANCELED){
            Log.e("request cancelled","To add the address")
        }
    }

  /*
    override fun onResume() {
        super.onResume()
        getAddressList()
    }

   */
    private fun getAddressList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAddressesList(this@AddressList)
    }

    fun successAddressListFromFirestore(addressList: ArrayList<Address>) {

        // Hide the progress dialog
        hideProgressDialog()

        if (addressList.size > 0) {

            rv_address_list.visibility = View.VISIBLE
            tv_no_address_found.visibility = View.GONE

            rv_address_list.layoutManager = LinearLayoutManager(this@AddressList)
            rv_address_list.setHasFixedSize(true)

           // START
            val addressAdapter = AddressListAdapter(this@AddressList, addressList, mSelectAddress)
            // END
            rv_address_list.adapter = addressAdapter

            // START
            if (!mSelectAddress) {
                val editSwipeHandler = object : SwipeToEditCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        val adapter = rv_address_list.adapter as AddressListAdapter
                        adapter.notifyEditItem(
                            this@AddressList,
                            viewHolder.adapterPosition
                        )
                    }
                }
                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(rv_address_list)


                val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        // Show the progress dialog.
                        showProgressDialog(resources.getString(R.string.please_wait))

                        FirestoreClass().deleteAddress(
                            this@AddressList,
                            addressList[viewHolder.adapterPosition].id
                        )
                    }
                }
                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(rv_address_list)
            }
        } else {
            rv_address_list.visibility = View.GONE
            tv_no_address_found.visibility = View.VISIBLE
        }
    }
    fun deleteAddressSuccess() {
        // Hide progress dialog.
        hideProgressDialog()
        Toast.makeText(
            this@AddressList,
            resources.getString(R.string.err_your_address_deleted_successfully),
            Toast.LENGTH_SHORT
        ).show()
        getAddressList()
    }
   private fun setupActionbar(){
        setSupportActionBar(toolbar_address_list_activity)
        val actionBar=supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_24dp)
        }
        toolbar_address_list_activity.setNavigationOnClickListener{onBackPressed()}
    }
}
