package com.example.multi_vendor.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.multi_vendor.models.CartItem
import com.example.multi_vendor.R
import com.example.multi_vendor.auxiliary.ShoppingCart
import javax.inject.Inject

class CartAdapter @Inject constructor(
    private val carts: ArrayList<CartItem>,
    private val context: Context,
    private val uid: String
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {


    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item: TextView = itemView.findViewById(R.id.commodity)
        val remove: TextView = itemView.findViewById(R.id.remove)
        val add: TextView = itemView.findViewById(R.id.add)
        val price: TextView = itemView.findViewById(R.id.price)
        val quantity: TextView = itemView.findViewById(R.id.qty1)
        val delete: ImageView = itemView.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return carts.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val shoppingCart = ShoppingCart(uid)

        val cart = carts[position]
        holder.price.text = cart.price.toInt().toString()

        val originalPrice = cart.price.toInt()
        var quantity = holder.quantity.text.toString().toInt()
        var price = holder.price.text.toString().toInt()
        holder.item.text = cart.item
        holder.remove.setOnClickListener {

            if (quantity == 1) {
                return@setOnClickListener
            } else {
                quantity--
                price -= originalPrice
                holder.quantity.text = quantity.toString()
                holder.price.text = context.getString(R.string.ksh, price.toString())
//                shoppingCart.editPrice(cart.itemId , price.toDouble())
            }
        }
        holder.add.setOnClickListener {
            quantity++
            price += originalPrice
            holder.quantity.text = quantity.toString()
            holder.price.text = context.getString(R.string.ksh, price.toString())
//            shoppingCart.editPrice(cart.itemId , price.toDouble())
        }
        holder.delete.setOnClickListener { shoppingCart.removeItem(cart.itemId) }

    }

}
