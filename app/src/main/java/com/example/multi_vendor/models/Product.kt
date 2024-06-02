package com.example.multi_vendor.models

data class Product(
    val category : String = "",
    val productId : String = "",
    val name : String = "",
    val description : String = "",
    val imageUrl : String = "",
    val vendor : String = "",
    val phone : String = "",
    val price : String = "",
    val availability : String = "Available",
    val uid : String = ""
)
