package com.example.multi_vendor.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.multi_vendor.models.Order
import com.example.multi_vendor.R
import com.example.multi_vendor.auxiliary.ShoppingCart
import javax.inject.Inject

class MyOrdersAdapter @Inject constructor(
    private val context: Context,
    private val orders : ArrayList<Order>,
    private val uid : String
)
    : RecyclerView.Adapter<MyOrdersAdapter.MyOrdersViewHolder>(){

//        interface OnCancelClick {
//            fun onCancelClicked(position: Int , holder : MyOrdersViewHolder)
//        }

    inner class MyOrdersViewHolder(itemView : View) : ViewHolder(itemView) {

        private val shoppingCart = ShoppingCart(uid)

        val product : TextView = itemView.findViewById(R.id.product)
        val price : TextView = itemView.findViewById(R.id.money)
        val vendor : TextView = itemView.findViewById(R.id.customerName)
        val call : TextView = itemView.findViewById(R.id.callV)
        private val status : TextView = itemView.findViewById(R.id.status)
        private val executed : TextView = itemView.findViewById(R.id.executed)
        val button : Button = itemView.findViewById(R.id.cancel)


        fun bind(order: Order) {
            button.setOnClickListener {
                shoppingCart.editCancel(order.orderId)
                notifyItemChanged(adapterPosition)
            }
            button.visibility = if (order.canceled || order.done) View.GONE else View.VISIBLE
            executed.visibility = if (order.canceled) View.VISIBLE else View.GONE
            executed.setTextColor(Color.RED)
            status.visibility = if (order.done) View.VISIBLE else View.GONE
            status.text = if (order.done) context.getString(R.string.delivered) else context.getString(
                R.string.pending
            )
            status.setTextColor(Color.GREEN)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrdersViewHolder {
        return MyOrdersViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.order_item1, parent , false))
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: MyOrdersViewHolder, position: Int) {

        val currentOrder = orders[position]
        holder.product.text = context.getString(R.string.product, currentOrder.quantity, currentOrder.productName)
        holder.price.text = context.getString(R.string.ksh, currentOrder.price)
        holder.vendor.text = currentOrder.vendor
        holder.call.text = currentOrder.vendorPhone
        holder.bind(currentOrder)
//        holder.button.setOnClickListener {
//
//            holder.status.text = context.getString(R.string.canceled)
//            holder.status.setTextColor(Color.RED)
//            holder.button.visibility = View.GONE
//            shoppingCart.editOrder(currentOrder.orderId)
//        }
    }
}