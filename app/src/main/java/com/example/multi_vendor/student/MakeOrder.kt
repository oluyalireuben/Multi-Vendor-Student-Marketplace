package com.example.multi_vendor.student

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.multi_vendor.R
import com.example.multi_vendor.databinding.ActivityMakeOrderBinding
import com.example.multi_vendor.models.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MakeOrder : AppCompatActivity() {
    private lateinit var binding: ActivityMakeOrderBinding
    private lateinit var database: DatabaseReference
    private lateinit var orderId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakeOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

         database = FirebaseDatabase.getInstance().reference
         orderId = database.child("Orders").push().key!!

        val amount = intent.getStringExtra("price").toString()
        val vendor = intent.getStringExtra("vendor").toString()
        val phone = intent.getStringExtra("phone").toString()
        val title = intent.getStringExtra("productTitle").toString()
        val uid = intent.getStringExtra("uid").toString()

        binding.commodity.text = title

        FirebaseDatabase.getInstance().getReference("App users/${FirebaseAuth.getInstance().currentUser!!.uid}")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tel = snapshot.child("phone").value.toString()
                    val name = snapshot.child("name").value.toString()

                    val sharedPreferences = getSharedPreferences("prefs" , MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("tel" , tel)
                    editor.putString("name" , name)
                    editor.apply()

                    binding.submit.setOnClickListener {
                        if (binding.location.text.isEmpty()) {
                            binding.location.error = "This is required!"
                            binding.location.requestFocus()
                            return@setOnClickListener
                        }
                        binding.progressBar6.visibility = View.VISIBLE
                        val order = Order(
                            orderId = orderId,
                            productName = title,
                            vendorPhone = phone,
                            location = binding.location.text.toString(),
                            price = binding.price.text.toString(),
                            vendor = vendor,
                            buyerUid =  FirebaseAuth.getInstance().currentUser!!.uid,
                            sellerUid = uid,
                            quantity = binding.qty1.text.toString(),
                            customerPhone = tel,
                            student = name
                        )

                        database.child("Orders").child(orderId).setValue(order)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    binding.progressBar6.visibility = View.GONE
                                    startActivity(Intent(this@MakeOrder , StudentDashboard::class.java))
                                    finish()
                                    Toast.makeText(this@MakeOrder , "Order submitted successfully!" , Toast.LENGTH_SHORT).show()
                                } else {
                                    binding.progressBar6.visibility = View.GONE
                                    Toast.makeText(this@MakeOrder , task.exception!!.localizedMessage , Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        binding.price.text = amount

        var quantity = binding.qty1.text.toString().toInt()
        val originalPrice = amount.toInt()
        var price = binding.price.text.toString().toInt()

        binding.remove.setOnClickListener {
            if (quantity == 1) {
                return@setOnClickListener
            } else {
                quantity --
                price -= originalPrice
                binding.qty1.text = quantity.toString()
                binding.price.text = getString(R.string.ksh, price.toString())
            }
        }
        binding.add.setOnClickListener {
            quantity ++
            price += originalPrice
            binding.qty1.text = quantity.toString()
            binding.price.text = getString(R.string.ksh, price.toString())
        }
    }
}