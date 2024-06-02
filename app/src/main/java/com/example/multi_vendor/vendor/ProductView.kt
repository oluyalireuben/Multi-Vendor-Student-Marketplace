package com.example.multi_vendor.vendor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.multi_vendor.R
import com.example.multi_vendor.databinding.ActivityProductViewBinding

class ProductView : AppCompatActivity() {
    private lateinit var binding: ActivityProductViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUrl = intent.getStringExtra("imageUrl").toString()
        val price = intent.getStringExtra("price").toString()
        val description = intent.getStringExtra("description").toString()
        val vendor = intent.getStringExtra("vendor").toString()
        val phone = intent.getStringExtra("phone").toString()
        val name = intent.getStringExtra("productTitle").toString()

        binding.titleText.text = name
        binding.description.text = description
        binding.phoneValue.text = phone
        binding.vendorValue.text = vendor
        binding.priceValue.text = getString(R.string.ksh, price)

        Glide.with(this).load(imageUrl).into(binding.expandedImage)
    }
}