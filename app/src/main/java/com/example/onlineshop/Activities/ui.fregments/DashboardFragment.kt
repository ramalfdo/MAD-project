package com.example.onlineshop.activity.activity.ui.fregments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import com.example.onlineshop.R
import com.example.onlineshop.activity.activity.CartList
import com.example.onlineshop.Activities.Settings
import com.example.onlineshop.adapters.DashboardItemListAdapter
import com.example.onlineshop.firestore.FirestoreClass
import com.example.onlineshop.models.Product
//import com.example.onlineshop.activity.activity.databinding.FragmentDashboardBinding
import com.example.onlineshop.databinding.FragmentDashboardBinding
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : BaseFragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        //val dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        when(id){
            R.id.action_settings ->{
                startActivity(Intent(activity, Settings::class.java))
                return true
            }
            R.id.action_cart ->{
                startActivity(Intent(activity, CartList::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun successDashboardItemsList(dashboardItemsList:ArrayList<Product>){
        hideProgressDialog()
        if (dashboardItemsList.size > 0){

            rv_dashboard_items.visibility = View.VISIBLE
            tv_no_dashboard_items_found.visibility = View.GONE
//display 2 items in one line
            rv_dashboard_items.layoutManager = GridLayoutManager(activity,2)
            rv_dashboard_items.setHasFixedSize(true)

            val adapter = DashboardItemListAdapter(requireActivity(),dashboardItemsList)
            rv_dashboard_items.adapter = adapter

            /*
            adapter.setOnClickListener(object : DashboardItemListAdapter.OnClickListener{
                override  fun onClick(position: Int,product: Product){
                    val intent = Intent(context, ProductDetails::class.java)
                    intent.putExtra(Constants.EXTRA_PRODUCT_ID,product.product_id)
                    intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.user_id)
                    startActivity(intent)
                }
            })

             */
        }
        else{
            rv_dashboard_items.visibility = View.GONE
            tv_no_dashboard_items_found.visibility = View.VISIBLE
        }
    }
    private fun getDashboardItemList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getDashboardItemsList(this@DashboardFragment)
    }
}