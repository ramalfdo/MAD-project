package com.example.onlineshop.activity.activity.ui.fregments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineshop.R
import com.example.onlineshop.adapters.SoldProductsListAdapter
import com.example.onlineshop.firestore.FirestoreClass
import com.example.onlineshop.models.SoldProduct
import kotlinx.android.synthetic.main.fragment_sold_product.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SoldProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SoldProductFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sold_product, container, false)
    }

    override fun onResume() {
        super.onResume()
        getSoldProductsList()
    }
    private fun getSoldProductsList() {

        showProgressDialog(resources.getString(R.string.please_wait))
        // Call function of Firestore class.
        FirestoreClass().getSoldProductsList(this@SoldProductFragment)
    }

    fun successSoldProductsList(soldProductsList: ArrayList<SoldProduct>) {
        // Hide Progress dialog.
        hideProgressDialog()
        if (soldProductsList.size > 0) {
            rv_sold_product_items.visibility = View.VISIBLE
            tv_no_sold_products_found.visibility = View.GONE

            rv_sold_product_items.layoutManager = LinearLayoutManager(activity)
            rv_sold_product_items.setHasFixedSize(true)

            val soldProductsListAdapter =
                SoldProductsListAdapter(requireActivity(), soldProductsList)
            rv_sold_product_items.adapter = soldProductsListAdapter
        } else {
            rv_sold_product_items.visibility = View.GONE
            tv_no_sold_products_found.visibility = View.VISIBLE
        }
    }

}