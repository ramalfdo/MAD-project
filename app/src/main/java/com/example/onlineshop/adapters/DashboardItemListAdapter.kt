package com.example.onlineshop.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineshop.R
import com.example.onlineshop.activity.activity.ProductDetails
import com.example.onlineshop.models.Product
import com.example.onlineshop.utils.Constants
import com.example.onlineshop.utils.GlideLoader
import kotlinx.android.synthetic.main.item_dashboard_layout.view.*

class DashboardItemListAdapter(
private val context: Context,
private var list: ArrayList<Product>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_dashboard_layout,
                parent,
                false
            )
        )
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    @SuppressLint("setText")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder){
            GlideLoader(context).loadProductPicture(model.image,holder.itemView.iv_dashboard_item_image)
            holder.itemView.tv_dashboard_item_title.text=model.title
            holder.itemView.tv_dashboard_item_price.text="Rs:${model.price}"

            holder.itemView.setOnClickListener{
                val intent = Intent(context, ProductDetails::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.user_id)
                context.startActivity(intent)
            }
            /*holder.itemView.setOnClickListener{
                if(onClickListener != null){
                    onClickListener!!.onClick(position,model)
                }
            }

             */
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

    interface OnClickListener{

        fun onClick(position: Int, product: Product)
    }

}