package com.example.multi_vendor.auxiliary

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.multi_vendor.models.Order
import com.google.firebase.database.FirebaseDatabase

object FirebaseHelper {
    private val database = FirebaseDatabase.getInstance().reference

    fun checkout(
        cartItems : List<Order>,
        context: Context,
        view : View,
        view1 : EditText,
        view2 : TextView,
        shoppingCart: ShoppingCart
    ) {
        val orderId = database.child("Orders").push().key!!
        for (item in cartItems) {
            val order = Order(
                orderId = orderId,
                productName = item.productName,
                price = item.price,
                quantity = item.quantity,
                vendorPhone = item.vendorPhone,
                customerPhone = item.customerPhone,
                location = item.location,
                vendor = item.vendor,
                student = item.student,
                buyerUid = item.buyerUid,
                sellerUid = item.sellerUid
            )
            database.child("Orders").child(orderId).setValue(order).addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    view.visibility = View.GONE
                    view1.setText("")
                    view1.clearFocus()
                    view2.text = "0.0"
                    Toast.makeText(context.applicationContext , "Done" , Toast.LENGTH_SHORT).show()
                    shoppingCart.clearCart()
                } else {
                    Toast.makeText(context.applicationContext , task.exception!!.localizedMessage , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}