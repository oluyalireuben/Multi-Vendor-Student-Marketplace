package com.example.multi_vendor.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.multi_vendor.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SettingsFragment : Fragment() {
    private var binding : FragmentSettingsBinding? = null
    private val _binding get() = binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater , container , false)

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        retrieveUserDetails()
        binding!!.edit.setOnClickListener {
            binding!!.userV.visibility = View.GONE
            binding!!.phoneV.visibility = View.GONE
            binding!!.editUsername.visibility = View.VISIBLE
            binding!!.editPhone.visibility = View.VISIBLE

            binding!!.edit.setOnClickListener {
                if (binding!!.editUsername.text.isEmpty()) {
                    binding!!.editUsername.error = "This is required!"
                    binding!!.editUsername.requestFocus()
                    return@setOnClickListener
                }
                if (binding!!.editPhone.text.isEmpty()) {
                    binding!!.editPhone.error = "This is required!"
                    binding!!.editPhone.requestFocus()
                    return@setOnClickListener
                }

                binding!!.progressBar6.visibility = View.VISIBLE
                FirebaseDatabase.getInstance()
                    .getReference("App users/${FirebaseAuth.getInstance().currentUser!!.uid}/name")
                    .setValue(binding!!.editUsername.text.toString())
                FirebaseDatabase.getInstance()
                    .getReference("App users/${FirebaseAuth.getInstance().currentUser!!.uid}/phone")
                    .setValue(binding!!.editPhone.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            retrieveUserDetails()
                            binding!!.progressBar6.visibility = View.GONE
                            binding!!.userV.visibility = View.VISIBLE
                            binding!!.phoneV.visibility = View.VISIBLE
                            binding!!.editUsername.visibility = View.GONE
                            binding!!.editPhone.visibility = View.GONE

                        }
                    }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun retrieveUserDetails() {
        FirebaseDatabase.getInstance()
            .getReference("App users/${FirebaseAuth.getInstance().currentUser!!.uid}")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding!!.userV.text = snapshot.child("name").value.toString()
                    binding!!.phoneV.text = snapshot.child("phone").value.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}