package com.example.multi_vendor.vendor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multi_vendor.R
import com.example.multi_vendor.auxiliary.ShoppingCart
import com.example.multi_vendor.adapters.OrdersAdapter
import com.example.multi_vendor.databinding.FragmentVendorOrdersBinding
import com.example.multi_vendor.models.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VendorOrdersFragment : Fragment() {

    private var binding: FragmentVendorOrdersBinding? = null
    private val _binding get() = binding!!

    private lateinit var orders: ArrayList<Order>
    private lateinit var onLongPress: OrdersAdapter.OnLongPress
    private lateinit var shoppingCart: ShoppingCart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVendorOrdersBinding.inflate(layoutInflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orders = ArrayList()
        binding!!.orders2.setHasFixedSize(true)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding!!.orders2.layoutManager = layoutManager

        shoppingCart = ShoppingCart(FirebaseAuth.getInstance().currentUser!!.uid)

        onLongPress = object : OrdersAdapter.OnLongPress {
            override fun onLongPressed(order: Order, constraintLayout: ConstraintLayout) {
                val popupMenu = PopupMenu(requireContext(), constraintLayout)
                popupMenu.inflate(R.menu.menu_item)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.delivered -> {
                            shoppingCart.editDone(order.orderId)
                            true
                        }

                        else -> {
                            false
                        }
                    }

                }
                popupMenu.show()
            }

        }
        FirebaseDatabase.getInstance().getReference("Orders").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orders.clear()
                snapshot.children.forEach { dataSnapshot ->
                    if (FirebaseAuth.getInstance().currentUser!!.uid == dataSnapshot.getValue(Order::class.java)!!.sellerUid) {
                        orders.add(dataSnapshot.getValue(Order::class.java)!!)
                    }
                }
                if (orders.isNotEmpty()) {
                    binding!!.orders2.visibility = View.VISIBLE
                    binding!!.loading.visibility = View.GONE
                } else {
                    binding!!.orders2.visibility = View.GONE
                    binding!!.loading.visibility = View.GONE
                    binding!!.empty.visibility = View.VISIBLE
                }
                binding!!.orders2.adapter = OrdersAdapter(requireContext(), orders, onLongPress)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}