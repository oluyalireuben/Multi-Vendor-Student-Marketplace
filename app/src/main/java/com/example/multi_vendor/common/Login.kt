package com.example.multi_vendor.common

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.multi_vendor.databinding.ActivityLoginBinding
import com.example.multi_vendor.student.StudentDashboard
import com.example.multi_vendor.vendor.VendorDashboard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textview10.setOnClickListener {
            startActivity(Intent(this , MainActivity::class.java))
        }

        binding.textview9.setOnClickListener {
            startActivity(Intent(this , ForgotPassword::class.java))
        }

        binding.button8.setOnClickListener { signInUser() }
    }

    private fun signInUser() {
        if (binding.edittext15.text.toString().trim().isEmpty()) {
            binding.edittext15.error  ="This is a required field!"
            binding.edittext15.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.edittext15.text.toString()).matches()) {
            binding.edittext15.error  ="Enter a valid email address!"
            binding.edittext15.requestFocus()
            return
        }
        if (binding.edittext16.text.toString().isEmpty()) {
            binding.edittext16.error  ="This is a required field!"
            binding.edittext16.requestFocus()
            return
        }

        binding.progressBar6.visibility = View.VISIBLE
        signInWithEmailAndPassword()
    }

    private fun signInWithEmailAndPassword() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.edittext15.text.toString() , binding.edittext16.text.toString())
            .addOnSuccessListener { authResult ->
                val uid = authResult.user!!.uid
                FirebaseDatabase.getInstance().getReference("App users/$uid")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val role = snapshot.child("role").value.toString()
                            if (role == "Student") {
                                startActivity(Intent(this@Login , StudentDashboard::class.java))
                                binding.progressBar6.visibility = View.GONE
                                binding.edittext16.setText("")
                                binding.edittext15.setText("")
                                return
                            }
                            if (role == "Vendor") {
                                startActivity(Intent(this@Login , VendorDashboard::class.java))
                                binding.progressBar6.visibility = View.GONE
                                binding.edittext16.setText("")
                                binding.edittext15.setText("")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
            }.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    binding.progressBar6.visibility = View.GONE
                    Toast.makeText(this , task.exception!!.localizedMessage , Toast.LENGTH_LONG).show()
                }
            }
    }

}