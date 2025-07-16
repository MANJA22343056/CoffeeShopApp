package com.example.aplikasicoffeeshop

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasicoffeeshop.adapter.ProdukAdapter
import com.example.aplikasicoffeeshop.api.ApiConfig
import com.example.aplikasicoffeeshop.api.ApiService
import com.example.aplikasicoffeeshop.model.Produk
import com.example.aplikasicoffeeshop.model.ProdukResponse
import com.example.aplikasicoffeeshop.model.TambahStokResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProdukFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var produkAdapter: ProdukAdapter
    private var produkList: MutableList<Produk> = mutableListOf()
    private var filteredProdukList: MutableList<Produk> = mutableListOf()

    private val token: String
        get() {
            val sharedPreferences = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
            return sharedPreferences?.getString("auth_token", "") ?: ""
        }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_produk, container, false)

        recyclerView = view.findViewById(R.id.RecyclerViewListProduk)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        produkAdapter = ProdukAdapter(filteredProdukList)
        recyclerView.adapter = produkAdapter

        fetchProduk()

        // Set SearchView listener
        val searchView: SearchView = view.findViewById(R.id.searchViewProduk)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Optional, can implement behavior on submit
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter products based on search query
                filterProducts(newText.orEmpty())
                return true
            }
        })

        // Set onClickListener for the "Tambah Stok" feature
        produkAdapter.setOnClickListener(object : ProdukAdapter.clickListener{
            override fun onItemClick(position: Int){
                // Handle product item click if needed
            }
            override fun onTambahStokClick(position: Int) {
                val selectedProduct = produkList[position]
                val inputJumlah = EditText(context).apply {
                    inputType = InputType.TYPE_CLASS_NUMBER
                    hint = "Masukkan jumlah penambahan stok"
                }
                val dialog = context?.let {
                    AlertDialog.Builder(it)
                        .setTitle("Jumlah Tambah Stok")
                        .setView(inputJumlah)
                        .setPositiveButton("Tambahkan"){ dialog, _ ->
                            tambahStok(selectedProduct.id, inputJumlah.text.toString().toInt())
                            dialog.dismiss()
                        }
                        .setNeutralButton("Batal") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                }
                dialog?.show()
            }
        })

        return view
    }

    private fun fetchProduk(){
        val retrofit = ApiConfig().getRetrofitClientInstance()
        val apiService = retrofit.create(ApiService::class.java)
        apiService.getProducts("Bearer $token").enqueue(object : Callback<ProdukResponse> {
            override fun onResponse(call: Call<ProdukResponse>, response: Response<ProdukResponse>) {
                if(response.isSuccessful){
                    val data = response.body()?.products ?: emptyList()
                    produkList.clear()
                    produkList.addAll(data)
                    filteredProdukList.clear()
                    filteredProdukList.addAll(data)
                    produkAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<ProdukResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun filterProducts(query: String) {
        val filteredList = produkList.filter {
            it.name.contains(query, ignoreCase = true)
        }
        filteredProdukList.clear()
        filteredProdukList.addAll(filteredList)
        produkAdapter.notifyDataSetChanged()
    }

    private fun tambahStok(productId: Int, quantity: Int) {
        val retrofit = ApiConfig().getRetrofitClientInstance()
        val apiService = retrofit.create(ApiService::class.java)
        apiService.tambahStok("Bearer $token", productId, quantity).enqueue(object : Callback<TambahStokResponse> {
            override fun onResponse(call: Call<TambahStokResponse>, response: Response<TambahStokResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val tambahStokResponse = response.body()
                    Toast.makeText(context, tambahStokResponse?.message, Toast.LENGTH_SHORT).show()
                    val updatedProduct = tambahStokResponse?.product
                    if (updatedProduct != null) {
                        updateProductInList(updatedProduct)
                    }
                } else {
                    Toast.makeText(context, "Gagal menambahkan stok: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TambahStokResponse>, t: Throwable) {
                Toast.makeText(context, "Gagal terhubung ke server.", Toast.LENGTH_LONG).show()
                Log.e("TambahStokError", "Error: ${t.message}")
            }
        })
    }

    private fun updateProductInList(updatedProduct: Produk) {
        // Perbarui produkList
        val indexInProdukList = produkList.indexOfFirst { it.id == updatedProduct.id }
        if (indexInProdukList != -1) {
            produkList[indexInProdukList] = updatedProduct
        }

        // Perbarui filteredProdukList
        val indexInFilteredProdukList = filteredProdukList.indexOfFirst { it.id == updatedProduct.id }
        if (indexInFilteredProdukList != -1) {
            filteredProdukList[indexInFilteredProdukList] = updatedProduct
            produkAdapter.notifyItemChanged(indexInFilteredProdukList)
        }
    }
}