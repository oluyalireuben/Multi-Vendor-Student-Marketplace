package com.example.multi_vendor.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.multi_vendor.models.Category
import com.example.multi_vendor.R

class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClick: OnCategoryClick
): RecyclerView.Adapter<CategoryAdapter.CategoryHolder>(){

    interface OnCategoryClick {
        fun onCategoryClicked(position: Int)
    }
    inner class CategoryHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val category : TextView = itemView.findViewById(R.id.categoryName)
        val item : CardView = itemView.findViewById(R.id.item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
       return CategoryHolder(LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent , false))
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        val currentCategory = categories[position]
        holder.category.text = currentCategory.name
        holder.item.setOnClickListener { onCategoryClick.onCategoryClicked(position) }
    }
}