package com.example.aplikasicoffeeshop.api

import com.example.aplikasicoffeeshop.model.AddTransactionRequest
import com.example.aplikasicoffeeshop.model.AddTransactionResponse
import com.example.aplikasicoffeeshop.model.LoginResponse
import com.example.aplikasicoffeeshop.model.Produk
import com.example.aplikasicoffeeshop.model.ProdukResponse
import com.example.aplikasicoffeeshop.model.TambahStokResponse
import com.example.aplikasicoffeeshop.model.TransaksiResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
        ): Call<LoginResponse>

    @POST("logout")
    fun logout(
        @Header("Authorization") token: String
    ): Call<Void>

    @GET("products")
    fun getProducts(
        @Header("Authorization") token: String
    ): Call<ProdukResponse>

    @FormUrlEncoded
    @POST("tambah-stok")
    fun tambahStok(
        @Header("Authorization") token: String,
        @Field("product_id") productId: Int,
        @Field("quantity") quantity: Int
    ): Call<TambahStokResponse>

    @GET("transaksi")
    fun getTransaksi(
        @Header("Authorization") token: String
    ): Call<List<TransaksiResponse>>

    @POST("tambah-transaksi")
    suspend fun addTransaction(
        @Header("Authorization") token: String,
        @Body request: AddTransactionRequest
    ): Response<AddTransactionResponse>

}