package com.example.multi_vendor.vendor

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.multi_vendor.databinding.FragmentVendorHomeBinding
import com.example.multi_vendor.models.Product
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.storage

class VendorHomeFragment : Fragment() , AdapterView.OnItemSelectedListener{

    private var binding: FragmentVendorHomeBinding? = null
    private val _binding get() = binding!!

    private val categories = arrayOf("All" , "Electronics" , "Shoes" , "Fashion" , "Edibles" , "Furniture" , "Beauty")

    private var selectedImageUri: Uri? = null
    private lateinit var database: DatabaseReference
    private lateinit var productId: String
    private lateinit var category: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVendorHomeBinding.inflate(layoutInflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().reference
        productId = database.child("Products").push().key!!

        binding!!.categorySpinner.onItemSelectedListener = this
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.categorySpinner.adapter = adapter

        binding!!.uploadBtn.setOnClickListener { uploadProduct() }
        binding!!.expandedImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            launcher.launch(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun uploadProduct() {
        if (binding!!.productName.text.isEmpty()) {
            binding!!.productName.error = "This is required!"
            binding!!.productName.requestFocus()
            return
        }
        if (binding!!.desc.text.isEmpty()) {
            binding!!.desc.error = "This is required!"
            binding!!.desc.requestFocus()
            return
        }
        if (binding!!.productPrice.text.isEmpty()) {
            binding!!.productPrice.error = "This is required!"
            binding!!.productPrice.requestFocus()
            return
        }

        binding!!.progressBar6.visibility = View.VISIBLE
        binding!!.uploadBtn.visibility = View.GONE

        uploadDetailsToFirebase()
    }

    private fun uploadDetailsToFirebase() {
        FirebaseDatabase.getInstance()
            .getReference("App users/${FirebaseAuth.getInstance().currentUser!!.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val vendor = snapshot.child("name").value.toString()
                    val phone = snapshot.child("phone").value.toString()
                    val product = Product(
                        category = category,
                        productId = productId,
                        name = binding!!.productName.text.toString(),
                        description = binding!!.desc.text.toString(),
                        imageUrl = "",
                        price = binding!!.productPrice.text.toString(),
                        vendor = vendor,
                        phone = phone,
                        uid = FirebaseAuth.getInstance().currentUser!!.uid
                    )

                    database.child("Products").child(productId)
                        .setValue(product).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                uploadImageToFirebase()
                                binding!!.productName.setText("")
                                binding!!.desc.setText("")
                                binding!!.productPrice.setText("")
                                binding!!.productPrice.clearFocus()
                                binding!!.progressBar6.visibility = View.GONE
                                binding!!.uploadBtn.visibility = View.VISIBLE
                                Toast.makeText(requireContext(), "Done", Toast.LENGTH_SHORT).show()
                            } else {
                                binding!!.progressBar6.visibility = View.GONE
                                binding!!.uploadBtn.visibility = View.VISIBLE
                                Toast.makeText(
                                    requireContext(),
                                    task.exception!!.localizedMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext() , error.message , Toast.LENGTH_SHORT).show()
                }

            })

    }


    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                selectedImageUri = data?.data
                binding!!.expandedImage.setImageURI(selectedImageUri)

            }
        }

    private fun uploadImageToFirebase() {
        selectedImageUri.let {
            val storageReference = Firebase.storage.reference
            val imagesReference = storageReference.child("images/${selectedImageUri?.lastPathSegment}")
            val uploadTask = imagesReference.putFile(selectedImageUri!!)

            uploadTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    imagesReference.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        database.child("Products")
                            .child(productId)
                            .child("imageUrl")
                            .setValue(imageUrl)
                    } .addOnFailureListener {
                        Toast.makeText(requireContext() , it.localizedMessage , Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext() , task.exception!!.localizedMessage , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        category = binding!!.categorySpinner.selectedItem.toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(requireContext() , "Set category" , Toast.LENGTH_SHORT).show()
    }
}