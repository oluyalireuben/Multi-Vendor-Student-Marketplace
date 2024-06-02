package com.example.multi_vendor.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multi_vendor.adapters.MyOrdersAdapter
import com.example.multi_vendor.databinding.FragmentOrdersBinding
import com.example.multi_vendor.models.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrdersFragment : Fragment() {
    private var binding: FragmentOrdersBinding? = null
    private val _binding get() = binding!!

    private lateinit var orders: ArrayList<Order>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(layoutInflater, container, false)

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orders = ArrayList()

        binding!!.orders1.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding!!.orders1.layoutManager = layoutManager

        FirebaseDatabase.getInstance().getReference("Orders").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orders.clear()
                snapshot.children.forEach { dataSnapshot ->
                    if (FirebaseAuth.getInstance().currentUser!!.uid == dataSnapshot.getValue(Order::class.java)!!.buyerUid) {
                        orders.add(dataSnapshot.getValue(Order::class.java)!!)
                    }
                }
                if (orders.isNotEmpty()) {
                    binding!!.empty.visibility = View.GONE
                    binding!!.orders1.visibility = View.VISIBLE
                    binding!!.loading.visibility = View.GONE
                } else {
                    binding!!.empty.visibility = View.VISIBLE
                    binding!!.orders1.visibility = View.GONE
                    binding!!.loading.visibility = View.GONE
                }
                binding!!.orders1.adapter = MyOrdersAdapter(
                    context = requireContext(),
                    orders = orders,
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