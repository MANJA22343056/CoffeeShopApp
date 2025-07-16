package com.example.aplikasicoffeeshop.model

data class AddTransactionRequest(
    val items: List<Item>,
    val payment: Int,
    val payment_method: String
)

data class Item(
    val product_id: Int,
    val quantity: Int
)
