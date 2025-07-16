package com.example.aplikasicoffeeshop.model

data class ProdukResponse(
    val message: String,
    val products: List<Produk>,
)

data class Produk(
    val id: Int,
    val name: String,
    val price: Int,
    val stock: Int
)
