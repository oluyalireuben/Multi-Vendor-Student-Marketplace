package com.example.multi_vendor.auxiliary

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.multi_vendor.models.CartItem
import com.example.multi_vendor.student.StudentDashboard
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShoppingCart(
    uid : String
) {
    private val database = FirebaseDatabase.getInstance().reference
    private val cartReference = database.child("Cart").child(uid)
    private val productReference = database.child("Products")
    private val orderReference = database.child("Orders")

    fun addItem(item : CartItem, context: Context) {
        cartReference.child(item.itemId)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val existingItem = snapshot.getValue(CartItem::class.java)
                if (existingItem == null) {
                    cartReference.child(item.itemId).setValue(item).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            try {
                                context.startActivity(Intent(context.applicationContext , StudentDashboard::class.java))
                                Toast.makeText(context.applicationContext , "Added successfully" , Toast.LENGTH_SHORT).show()
                            } catch (exception : Exception) {
                                Toast.makeText(context.applicationContext , exception.localizedMessage , Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context.applicationContext , task.exception!!.localizedMessage , Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context.applicationContext , "Item is already in cart" , Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun removeItem(itemId : String) {
        cartReference.child(itemId).removeValue()
    }

    fun deleteProduct(itemId : String) {
        productReference.child(itemId).removeValue()
    }

    fun clearCart() {
        cartReference.removeValue()
    }
    fun editCancel(orderId : String) {
        orderReference.child(orderId).child("canceled").setValue(true)
    }

    fun editDone(orderId : String) {
        orderReference.child(orderId).child("done").setValue(true)
    }

    fun editPrice(itemId: String, price : Double) {
        cartReference.child(itemId).child("price").setValue(price)
    }
}