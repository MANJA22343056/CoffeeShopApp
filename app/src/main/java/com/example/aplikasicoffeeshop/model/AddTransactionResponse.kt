package com.example.aplikasicoffeeshop.model

data class AddTransactionResponse(
    val message: String,
    val transaction: Transaction
)

data class Transaction(
    val id: Int,
    val user_id: Int,
    val total_price: Int,
    val payment: Int,
    val change: Int,
    val payment_method: String,
    val details: List<TransactionDetail>
)

data class TransactionDetail(
    val product_id: Int,
    val quantity: Int,
    val sub_total: Int
)
