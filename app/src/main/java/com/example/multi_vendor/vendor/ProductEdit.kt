package com.example.multi_vendor.vendor

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.multi_vendor.databinding.ActivityProductEditBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProductEdit : AppCompatActivity() {
    private lateinit var binding: ActivityProductEditBinding
    private lateinit var database: DatabaseReference
    private lateinit var productId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference

        val name = intent.getStringExtra("productTitle").toString()
        val price = intent.getStringExtra("price").toString()
        val description = intent.getStringExtra("description").toString()
        val availability = intent.getStringExtra("availability").toString()
        val imageUrl = intent.getStringExtra("imageUrl").toString()
        productId = intent.getStringExtra("productId").toString()

        Glide.with(this).load(imageUrl).into(binding.expandedImage)

        binding.productName.setText(name)
        binding.desc.setText(description)
        binding.productPrice.setText(price)

        if (availability == "Available") {
            binding.checkbox.isChecked = true
        }

        binding.uploadBtn.setOnClickListener { uploadProduct() }
    }

    private fun uploadProduct() {
        if (binding.productName.text.isEmpty()) {
            binding.productName.error = "This is required!"
            binding.productName.requestFocus()
            return
        }
        if (binding.desc.text.isEmpty()) {
            binding.desc.error = "This is required!"
            binding.desc.requestFocus()
            return
        }
        if (binding.productPrice.text.isEmpty()) {
            binding.productPrice.error = "This is required!"
            binding.productPrice.requestFocus()
            return
        }
        if (binding.productName.text.isEmpty()) {
            Toast.makeText(this, "Set availability", Toast.LENGTH_LONG).show()
            return
        }

        binding.progressBar6.visibility = View.VISIBLE
        binding.uploadBtn.visibility = View.GONE

        uploadDetailsToFirebase()
    }

    private fun uploadDetailsToFirebase() {

        database.child("Products").child(productId).child("price")
            .setValue(binding.productPrice.text.toString())
        database.child("Products").child(productId).child("description")
            .setValue(binding.desc.text.toString())
        database.child("Products").child(productId).child("name")
            .setValue(binding.productName.text.toString())
        if (binding.checkbox.isChecked) {
            database.child("Products").child(productId).child("availability")
                .setValue("Available")
        } else {
            database.child("Products").child(productId).child("availability")
                .setValue("Out of stock")
        }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.productName.setText("")
                    binding.desc.setText("")
                    binding.productPrice.setText("")
                    binding.progressBar6.visibility = View.GONE
                    binding.uploadBtn.visibility = View.VISIBLE
                    Toast.makeText(this@ProductEdit, "Done", Toast.LENGTH_SHORT).show()
                } else {
                    binding.progressBar6.visibility = View.GONE
                    binding.uploadBtn.visibility = View.VISIBLE
                    Toast.makeText(
                        this@ProductEdit,
                        task.exception!!.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }


}
