package com.example.multi_vendor.vendor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multi_vendor.adapters.VendorSettingsAdapter
import com.example.multi_vendor.databinding.FragmentVendorSettingsBinding
import com.example.multi_vendor.models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VendorSettingsFragment : Fragment() {

    private lateinit var binding: FragmentVendorSettingsBinding

    private lateinit var products: ArrayList<Product>
    private lateinit var onProductClick: VendorSettingsAdapter.OnProductClick

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVendorSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        products = ArrayList()

        retrieveUserDetails()
        binding.products.setHasFixedSize(true)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.products.layoutManager = layoutManager


        onProductClick = object : VendorSettingsAdapter.OnProductClick {
            override fun onProductClicked(position: Int) {
                val currentProduct = products[position]
                requireContext().startActivity(
                    Intent(requireContext(), ProductView::class.java)
                        .putExtra("productTitle", currentProduct.name)
                        .putExtra("imageUrl", currentProduct.imageUrl)
                        .putExtra("price", currentProduct.price)
                        .putExtra("vendor", currentProduct.vendor)
                        .putExtra("phone", currentProduct.phone)
                        .putExtra("description", currentProduct.description)
                )
            }

            override fun onEdit(position: Int) {
                val currentProduct = products[position]
                requireContext().startActivity(
                    Intent(requireContext(), ProductEdit::class.java)
                        .putExtra("productTitle", currentProduct.name)
                        .putExtra("price", currentProduct.price)
                        .putExtra("description", currentProduct.description)
                        .putExtra("availability", currentProduct.availability)
                        .putExtra("imageUrl", currentProduct.imageUrl)
                        .putExtra("productId", currentProduct.productId)
                )
            }

        }
        binding.edit.setOnClickListener {
            binding.userV.visibility = View.GONE
            binding.phoneV.visibility = View.GONE
            binding.editUsername.visibility = View.VISIBLE
            binding.editPhone.visibility = View.VISIBLE

            binding.edit.setOnClickListener {
                if (binding.editUsername.text.isEmpty()) {
                    binding.editUsername.error = "This is required!"
                    binding.editUsername.requestFocus()
                    return@setOnClickListener
                }
                if (binding.editPhone.text.isEmpty()) {
                    binding.editPhone.error = "This is required!"
                    binding.editPhone.requestFocus()
                    return@setOnClickListener
                }
                binding.progressBar6.visibility = View.VISIBLE
                FirebaseDatabase.getInstance()
                    .getReference("App users/${FirebaseAuth.getInstance().currentUser!!.uid}/name")
                    .setValue(binding.editUsername.text.toString())
                FirebaseDatabase.getInstance()
                    .getReference("App users/${FirebaseAuth.getInstance().currentUser!!.uid}/phone")
                    .setValue(binding.editPhone.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            retrieveUserDetails()
                            binding.progressBar6.visibility = View.GONE
                            binding.userV.visibility = View.VISIBLE
                            binding.phoneV.visibility = View.VISIBLE
                            binding.editUsername.visibility = View.GONE
                            binding.editPhone.visibility = View.GONE

                        }
                    }
            }

        }
        FirebaseDatabase.getInstance().getReference("Products")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    products.clear()
                    snapshot.children.forEach { dataSnapshot ->
                        val name = dataSnapshot.child("name").value.toString()
                        val uid = dataSnapshot.child("uid").value.toString()
                        val productId = dataSnapshot.child("productId").value.toString()
                        val description = dataSnapshot.child("description").value.toString()
                        val imageUrl = dataSnapshot.child("imageUrl").value.toString()
                        val vendor = dataSnapshot.child("vendor").value.toString()
                        val phone = dataSnapshot.child("phone").value.toString()
                        val availability = dataSnapshot.child("availability").value.toString()
                        val price = dataSnapshot.child("price").value.toString()

                        val product = Product(
                            name = name,
                            uid = uid,
                            productId = productId,
                            description = description,
                            imageUrl = imageUrl,
                            vendor = vendor,
                            phone = phone,
                            availability = availability,
                            price = price
                        )
                        if (FirebaseAuth.getInstance().currentUser!!.uid == uid) {
                            products.add(product)
                        }
                    }
                    if (products.isNotEmpty()) {
                            binding.products.visibility = View.VISIBLE
                    } else {
                            binding.products.visibility = View.GONE
                            binding.empty.visibility = View.VISIBLE
                    }
                        binding.products.adapter = VendorSettingsAdapter(
                            products = products,
                            onProductClick = onProductClick,
                            context = requireContext(),
                            uid = FirebaseAuth.getInstance().currentUser!!.uid
                        )
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        binding = null
//    }

    private fun retrieveUserDetails() {
        FirebaseDatabase.getInstance()
            .getReference("App users/${FirebaseAuth.getInstance().currentUser!!.uid}")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.userV.text = snapshot.child("name").value.toString()
                    binding.phoneV.text = snapshot.child("phone").value.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}