package com.example.multi_vendor.models

data class Order(
    val orderId : String = "",
    val productName : String = "",
    val vendorPhone : String = "",
    val customerPhone : String = "",
    val location : String = "",
    val price : String = "",
    val vendor : String = "",
    val student : String = "",
    val buyerUid : String = "",
    val sellerUid : String = "",
    val quantity : String = "",
    val done : Boolean = false,
    val canceled : Boolean = false
)
