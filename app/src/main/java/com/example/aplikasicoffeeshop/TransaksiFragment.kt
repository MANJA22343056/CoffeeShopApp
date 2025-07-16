package com.example.aplikasicoffeeshop

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasicoffeeshop.adapter.TransaksiAdapter
import com.example.aplikasicoffeeshop.api.ApiConfig
import com.example.aplikasicoffeeshop.api.ApiService
import com.example.aplikasicoffeeshop.model.TransaksiResponse
import retrofit2.*

class TransaksiFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var transaksiAdapter: TransaksiAdapter
    private var transaksiList: List<TransaksiResponse> = listOf()
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
        val view = inflater.inflate(R.layout.fragment_transaksi, container, false)

        val buttonTambahTransaksi = view.findViewById<Button>(R.id.buttonTambahTransaksi)
        buttonTambahTransaksi.setOnClickListener {
            val intent = Intent(activity, TambahTransaksiActivity::class.java)
            startActivity(intent)
        }

        recyclerView = view.findViewById(R.id.RecyclerViewTransaksi)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        transaksiAdapter = TransaksiAdapter(transaksiList)
        recyclerView.adapter = transaksiAdapter

        fetchTransaksi()

        return view
    }

    private fun fetchTransaksi(){
        val retrofit = ApiConfig().getRetrofitClientInstance()
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getTransaksi("Bearer $token")
        call.enqueue(object : Callback<List<TransaksiResponse>> {
            override fun onResponse(
                call: Call<List<TransaksiResponse>>,
                response: Response<List<TransaksiResponse>>
            ) {
                if (response.isSuccessful) {
                    val transaksiList = response.body() ?: emptyList()
                    transaksiAdapter = TransaksiAdapter(transaksiList)
                    recyclerView.adapter = transaksiAdapter

                    transaksiAdapter.setOnClickListener(object : TransaksiAdapter.clickListener {
                        override fun onItemClick(position: Int) {
                            // Tindakan ketika item di klik
                        }
                    })
                } else {
                    Toast.makeText(context, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    Log.e("error", response.body().toString())
                }
            }
            override fun onFailure(call: Call<List<TransaksiResponse>>, t: Throwable) {

            }
        })
    }

}