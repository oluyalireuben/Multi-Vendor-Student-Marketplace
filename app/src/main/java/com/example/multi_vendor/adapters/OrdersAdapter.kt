package com.example.multi_vendor.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.multi_vendor.models.Order
import com.example.multi_vendor.R
import javax.inject.Inject

class OrdersAdapter @Inject constructor(
    private val context: Context,
    private val orders: ArrayList<Order>,
    private val onLongPress: OnLongPress
) : RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    interface OnLongPress {
        fun onLongPressed(order: Order, constraintLayout: ConstraintLayout)
    }
    inner class OrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val product: TextView = itemView.findViewById(R.id.product)
        val price: TextView = itemView.findViewById(R.id.money)
        val customer: TextView = itemView.findViewById(R.id.customerName)
        val call: TextView = itemView.findViewById(R.id.callV)
        val location: TextView = itemView.findViewById(R.id.locV)
        val item: ConstraintLayout = itemView.findViewById(R.id.item)
        private val status: TextView = itemView.findViewById(R.id.active)
        private val executed: TextView = itemView.findViewById(R.id.executed)
//        val done : Button = itemView.findViewById(R.id.done)

        fun bind(order: Order) {
            status.visibility = if (order.done) View.GONE else View.VISIBLE
            status.text = if (order.canceled) context.getString(R.string.canceled) else context.getString(
                R.string.active
            )
            status.setTextColor(if (order.canceled) Color.RED else Color.GREEN)

            executed.visibility = if (order.done) View.VISIBLE else View.GONE
            executed.setTextColor(Color.GREEN)

            call.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.setData(Uri.parse("tel:${order.customerPhone}"))
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {

        val currentOrder = orders[position]

        holder.call.text = currentOrder.customerPhone
        holder.price.text = context.getString(R.string.ksh, currentOrder.price)
        holder.customer.text = currentOrder.student
        holder.product.text = context.getString(R.string.product, currentOrder.quantity, currentOrder.productName)
        holder.location.text = currentOrder.location

        holder.item.setOnLongClickListener {
            onLongPress.onLongPressed(currentOrder , holder.item)
            true
        }
        holder.bind(currentOrder)
    }
}