package com.example.multi_vendor.models

data class CartItem(
    val item : String = "",
    val price : Double = 0.0,
    val uid : String = "",
    val itemId : String = "",
    val quantity : Int = 1,
)
