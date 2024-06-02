package com.example.multi_vendor.common


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.multi_vendor.databinding.ActivityMainBinding
import com.example.multi_vendor.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private val users = arrayOf("", "Student", "Vendor")
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        binding.spinner.onItemSelectedListener = this
        val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, users)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter


        binding.button1.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }

        binding.registerBtn.setOnClickListener {
            registerUser()
        }
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val value = binding.spinner.selectedItem.toString()
        if (value == "Student") {
            binding.selectedText.text = value
            binding.selectedText.visibility = View.VISIBLE
            return
        }

        if (value == "Vendor") {
            binding.selectedText.text = value
            binding.selectedText.visibility = View.VISIBLE
            return
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun registerUser() {
        if (binding.edittext17.text.toString().trim().isEmpty()) {
            binding.edittext17.error = "This is a required field!"
            binding.edittext17.requestFocus()
            return
        }
        if (binding.edittext18.text.toString().isEmpty()) {
            binding.edittext18.error = "This is a required field!"
            binding.edittext18.requestFocus()
            return
        }
        if (binding.edittext15.text.toString().trim().isEmpty()) {
            binding.edittext15.error = "This is a required field!"
            binding.edittext15.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.edittext15.text.toString().trim()).matches()) {
            binding.edittext15.error = "Enter a valid email address!"
            binding.edittext15.requestFocus()
            return
        }
        if (binding.edittext16.text.toString().trim().isEmpty()) {
            binding.edittext16.error = "This is a required field!"
            binding.edittext16.requestFocus()
            return
        }
        if (binding.selectedText.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Select role", Toast.LENGTH_LONG).show()
            return
        }
        binding.progressBar6.visibility = View.VISIBLE

        createUserWithEmailAndPassword()
    }

    private fun createUserWithEmailAndPassword() {
        auth.createUserWithEmailAndPassword(binding.edittext15.text.toString(), binding.edittext16.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(
                            name = binding.edittext17.text.toString(),
                            email = binding.edittext15.text.toString(),
                            phone = binding.edittext18.text.toString(),
                            password = binding.edittext16.text.toString(),
                            role = binding.selectedText.text.toString()
                        )
                    FirebaseDatabase.getInstance().getReference("App users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(user)
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                startActivity(Intent(this@MainActivity, Login::class.java))
                                binding.progressBar6.visibility = View.GONE
                                binding.edittext15.setText("")
                                binding.edittext17.setText("")
                                binding.edittext18.setText("")
                                binding.edittext16.setText("")
                                binding.edittext16.clearFocus()
                                binding.selectedText.text = ""


                            } else {
                                Toast.makeText(this@MainActivity, task1.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                                binding.progressBar6.visibility = View.GONE
                            }
                        }
                } else {
                    Toast.makeText(this@MainActivity, task.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                    binding.progressBar6.visibility = View.GONE
                }
            }
    }

}