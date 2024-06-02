package com.example.multi_vendor.student

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.multi_vendor.R
import com.example.multi_vendor.auxiliary.ShoppingCart
import com.example.multi_vendor.databinding.ActivityProductDetailsBinding
import com.example.multi_vendor.models.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProductDetails : AppCompatActivity() {
    private lateinit var binding : ActivityProductDetailsBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var cartItemId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseReference = FirebaseDatabase.getInstance().reference
        cartItemId = databaseReference.child("Cart").push().key!!

        val shoppingCart = ShoppingCart(FirebaseAuth.getInstance().currentUser!!.uid)

        binding.arrowBack.setOnClickListener {
            navigateUpTo(Intent(this , StudentDashboard::class.java))
        }

        val productTitle = intent.getStringExtra("productTitle").toString()
        val description = intent.getStringExtra("description").toString()
        val price = intent.getStringExtra("price").toString()
        val vendor = intent.getStringExtra("vendor").toString()
        val image = intent.getStringExtra("image").toString()
        val phone = intent.getStringExtra("phone").toString()
        val uid = intent.getStringExtra("uid").toString()
        val productId = intent.getStringExtra("productId").toString()
        val availability = intent.getStringExtra("availability").toString()

        val sharedPreferences = getSharedPreferences("sharedPrefs" , MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("productTitle" , productTitle)
        editor.putString("price" , price)
        editor.putString("vendor" , vendor)
        editor.putString("image" , image)
        editor.putString("phone" , phone)
        editor.putString("uid" , uid)
        editor.putString("productId" , productId)
        editor.apply()

        if (availability == "Out of stock") {
            binding.buy.visibility = View.GONE
            binding.addToCart.visibility = View.GONE
        }

        binding.titleText.text = productTitle
        binding.description.text = description
        binding.priceValue.text = getString(R.string.ksh, price)
        binding.vendorValue.text = vendor
        binding.phoneValue.text = phone

        binding.phoneValue.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.setData(Uri.parse("tel:$phone"))
            startActivity(intent)
        }
        // Use glide to inflate the image
        Glide.with(this).load(image).error(R.drawable.baseline_image_24).error(R.drawable.baseline_image_24).into(binding.expandedImage)

        binding.buy.setOnClickListener {
            startActivity(Intent(this , MakeOrder::class.java)
                .putExtra("price" , price)
                .putExtra("productTitle" , productTitle)
                .putExtra("vendor" , vendor)
                .putExtra("phone" , phone)
                .putExtra("uid" , uid)
            )
        }

        binding.addToCart.setOnClickListener {

            val cart = CartItem(
                item = productTitle,
                price = price.toDouble(),
                uid = FirebaseAuth.getInstance().currentUser!!.uid,
                itemId = productId
            )
            shoppingCart.addItem(cart , this)
        }
    }
}