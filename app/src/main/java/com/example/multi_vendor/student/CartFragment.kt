package com.example.multi_vendor.student

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multi_vendor.auxiliary.FirebaseHelper
import com.example.multi_vendor.R
import com.example.multi_vendor.auxiliary.ShoppingCart
import com.example.multi_vendor.adapters.CartAdapter
import com.example.multi_vendor.databinding.FragmentCartBinding
import com.example.multi_vendor.models.CartItem
import com.example.multi_vendor.models.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CartFragment : Fragment() {
    private lateinit var carts: ArrayList<CartItem>
    private lateinit var shoppingCart: ShoppingCart

    private var binding: FragmentCartBinding? = null
    private val _binding get() = binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(layoutInflater , container , false)

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        carts = ArrayList()
        shoppingCart = ShoppingCart(FirebaseAuth.getInstance().currentUser!!.uid)

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding!!.cartRecyclerView.layoutManager = layoutManager

        binding!!.totalValue.visibility = View.GONE
        binding!!.total.visibility = View.GONE

        val sharedPreferences = requireContext().getSharedPreferences("sharedPrefs" , AppCompatActivity.MODE_PRIVATE)
        val productName = sharedPreferences.getString("productTitle" , "").toString()
        val vendor = sharedPreferences.getString("vendor" , "").toString()
        val vendorPhone = sharedPreferences.getString("phone" , "").toString()
        val valuePrice = sharedPreferences.getString("price" , "").toString()
        val sellerUid = sharedPreferences.getString("uid" , "").toString()

        val preferences = requireContext().getSharedPreferences("prefs" , AppCompatActivity.MODE_PRIVATE)
        val customerPhone = preferences.getString("tel" , "Unavailable").toString()
        val student = preferences.getString("name" , "Unavailable").toString()

        binding!!.submit.setOnClickListener {
            if (binding!!.location.text.isEmpty()) {
                binding!!.location.error = "This is required!"
                binding!!.location.requestFocus()
                return@setOnClickListener
            }
            binding!!.loading.visibility = View.VISIBLE
            val order = Order(
                productName = productName,
                vendor = vendor,
                customerPhone = customerPhone,
                vendorPhone = vendorPhone,
                location = binding!!.location.text.toString(),
                price = valuePrice,
                student = student,
                buyerUid = FirebaseAuth.getInstance().currentUser!!.uid,
                sellerUid = sellerUid,
                quantity = ""
            )
            FirebaseHelper.checkout(
                listOf(order),
                requireContext(),
                binding!!.loading,
                binding!!.location,
                binding!!.totalValue,
                shoppingCart
            )
        }

        FirebaseDatabase.getInstance()
            .getReference("Cart/${FirebaseAuth.getInstance().currentUser!!.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    carts.clear()
                    snapshot.children.forEach { dataSnapshot ->
                        val item = dataSnapshot.child("item").value.toString()
                        val price = dataSnapshot.child("price").value.toString()
                        val uid = dataSnapshot.child("uid").value.toString()
                        val itemId = dataSnapshot.child("itemId").value.toString()

                        val cart = CartItem(
                            item = item,
                            price = price.toDouble(),
                            uid = uid,
                            itemId = itemId
                        )
                        carts.add(cart)
                    }
                    if (carts.isNotEmpty()) {
                        binding!!.cartRecyclerView.visibility = View.VISIBLE
                        binding!!.loading.visibility = View.GONE
                        binding!!.totalValue.text = getString(R.string.ksh, carts.sumOf { it.price }.toString())
                    } else {
                        binding!!.cartRecyclerView.visibility = View.GONE
                        binding!!.loading.visibility = View.GONE
                        binding!!.totalValue.text = "0.0"
                        binding!!.empty.visibility = View.VISIBLE
                        binding!!.submit.visibility = View.GONE
                        binding!!.location.visibility = View.GONE
                    }

                    binding!!.cartRecyclerView.adapter = CartAdapter(
                        carts,
                        requireContext(),
                        uid = FirebaseAuth.getInstance().currentUser!!.uid
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}