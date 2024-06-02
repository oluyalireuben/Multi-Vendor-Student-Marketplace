package com.example.multi_vendor.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.multi_vendor.R
import com.example.multi_vendor.auxiliary.ShoppingCart
import com.example.multi_vendor.models.Product
import javax.inject.Inject

class VendorSettingsAdapter @Inject constructor(
    private val products : ArrayList<Product>,
    private val onProductClick: OnProductClick,
    private val context: Context,
    private val uid : String
): RecyclerView.Adapter<VendorSettingsAdapter.VendorSettingsViewHolder>(){

    interface OnProductClick {
        fun onProductClicked(position: Int)
        fun onEdit(position: Int)
    }
    inner class VendorSettingsViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.productTitle)
        val delete : ImageView = itemView.findViewById(R.id.delete)
        val edit : ImageView = itemView.findViewById(R.id.edit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorSettingsViewHolder {
        return VendorSettingsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.edit_product_item, parent , false))
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: VendorSettingsViewHolder, position: Int) {
        val currentProduct = products[position]
        val shoppingCart = ShoppingCart(uid)
        holder.name.text = currentProduct.name
        holder.name.setOnClickListener { onProductClick.onProductClicked(position) }
        holder.delete.setOnClickListener {
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle("Alert")
                .setMessage("This action deletes this product permanently!")
                .setPositiveButton(
                    "Confirm"
                ) { dialog, _ ->
                    shoppingCart.deleteProduct(currentProduct.productId)
                    dialog.dismiss()
                }
                .setNegativeButton(
                    "Cancel"
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .create()
                .show()

        }
        holder.edit.setOnClickListener { onProductClick.onEdit(position) }
    }
}