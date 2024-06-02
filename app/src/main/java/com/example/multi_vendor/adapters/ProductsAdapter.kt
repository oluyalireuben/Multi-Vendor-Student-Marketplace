package com.example.multi_vendor.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.multi_vendor.models.Product
import com.example.multi_vendor.R
import javax.inject.Inject

class ProductsAdapter @Inject constructor(
    private val context: Context,
    private val onProductClick: OnProductClick,
    private var products : ArrayList<Product>
): RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {

    interface OnProductClick {
        fun onProductClickListener(position: Int)
    }
    inner class ProductsViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val productName : TextView = itemView.findViewById(R.id.productTitle)
        val vendor : TextView = itemView.findViewById(R.id.vendor)
        val price : TextView = itemView.findViewById(R.id.price)
        val availability : TextView = itemView.findViewById(R.id.availability)
        val image : ImageView = itemView.findViewById(R.id.productImage)
        val product : ConstraintLayout = itemView.findViewById(R.id.product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent , false))
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val currentProduct = products[position]
        holder.productName.text = currentProduct.name
        holder.vendor.text = currentProduct.vendor
        holder.price.text = context.getString(R.string.ksh, currentProduct.price)
        holder.availability.text = currentProduct.availability

        Glide.with(context).load(currentProduct.imageUrl).placeholder(R.drawable.baseline_image_24).error(
            R.drawable.baseline_image_24
        ).into(holder.image)
        holder.product.setOnClickListener { onProductClick.onProductClickListener(position) }
    }

}