package com.example.aplikasicoffeeshop.model

data class LoginResponse(
    val message: String,
    val token: String,
    val user: User
)

data class User (
    val id: Int,
    val username: String,
    val name: String
)