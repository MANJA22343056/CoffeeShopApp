package com.example.aplikasicoffeeshop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.aplikasicoffeeshop.api.ApiConfig
import com.example.aplikasicoffeeshop.api.ApiService
import com.example.aplikasicoffeeshop.model.AddTransactionRequest
import com.example.aplikasicoffeeshop.model.Item
import com.example.aplikasicoffeeshop.model.Produk
import com.example.aplikasicoffeeshop.model.ProdukResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TambahTransaksiActivity : AppCompatActivity() {
    private val retrofit = ApiConfig().getRetrofitClientInstance()
    private val apiService = retrofit.create(ApiService::class.java)
    private val token: String by lazy {
        getSharedPreferences("user", MODE_PRIVATE).getString("auth_token", "") ?: ""
    }
    private lateinit var products: List<Produk>
    private lateinit var productContainer: LinearLayout
    private lateinit var paymentInput: EditText
    private lateinit var paymentMethodInput: EditText
    private lateinit var processButton: Button
    private lateinit var totalPriceTextView: TextView
    private var totalPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_transaksi)

        productContainer = findViewById(R.id.productContainer)
        paymentInput = findViewById(R.id.paymentInput)
        paymentMethodInput = findViewById(R.id.inputTextMetodePembayaran)
        processButton = findViewById(R.id.processButton)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)

        apiService.getProducts("Bearer $token").enqueue(object : Callback<ProdukResponse> {
            override fun onResponse(call: Call<ProdukResponse>, response: Response<ProdukResponse>) {
                if (response.isSuccessful) {
                    products = response.body()?.products ?: emptyList()
                    displayProducts(products)
                } else {
                    Toast.makeText(this@TambahTransaksiActivity, "Gagal memuat produk", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProdukResponse>, t: Throwable) {
                Toast.makeText(this@TambahTransaksiActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        processButton.setOnClickListener {
            processTransaction()
        }
    }

    private fun displayProducts(products: List<Produk>) {
        for (product in products) {
            val itemView = LayoutInflater.from(this).inflate(R.layout.item_product, productContainer, false)
            val productNameTextView = itemView.findViewById<TextView>(R.id.productNameTextView)
            val productPriceTextView = itemView.findViewById<TextView>(R.id.productPriceTextView)
            val quantityInput = itemView.findViewById<EditText>(R.id.quantityInput)

            productNameTextView.text = product.name
            productPriceTextView.text = product.price.toString()

            quantityInput.addTextChangedListener {
                calculateTotalPrice()
            }

            productContainer.addView(itemView)
        }
    }

    private fun calculateTotalPrice() {
        totalPrice = 0
        for (i in 0 until productContainer.childCount) {
            val itemView = productContainer.getChildAt(i)
            val productPriceTextView = itemView.findViewById<TextView>(R.id.productPriceTextView)
            val quantityInput = itemView.findViewById<EditText>(R.id.quantityInput)

            val price = productPriceTextView.text.toString().toIntOrNull() ?: 0
            val quantity = quantityInput.text.toString().toIntOrNull() ?: 0

            totalPrice += price * quantity
        }
        totalPriceTextView.text = "Total: $totalPrice"
    }

    private fun processTransaction() {
        val items = mutableListOf<Item>()
        for (i in 0 until productContainer.childCount) {
            val itemView = productContainer.getChildAt(i)
            val productPriceTextView = itemView.findViewById<TextView>(R.id.productPriceTextView)
            val quantityInput = itemView.findViewById<EditText>(R.id.quantityInput)
            val productNameTextView = itemView.findViewById<TextView>(R.id.productNameTextView)

            val product = products.find { it.name == productNameTextView.text.toString() }
            val quantity = quantityInput.text.toString().toIntOrNull() ?: 0

            if (product != null && quantity > 0) {
                items.add(Item(product_id = product.id, quantity = quantity))
            }
        }

        val payment = paymentInput.text.toString().toIntOrNull() ?: 0
        val paymentMethod = paymentMethodInput.text.toString()

        if (items.isEmpty() || payment == 0 || paymentMethod.isEmpty()) {
            Toast.makeText(this, "Harap isi semua kolom dengan benar", Toast.LENGTH_SHORT).show()
            return
        }

        val addTransactionRequest = AddTransactionRequest(
            items = items,
            payment = payment,
            payment_method = paymentMethod
        )

        Log.d("TambahTransaksiActivity", "Request: $addTransactionRequest")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.addTransaction("Bearer $token", addTransactionRequest)
                Log.d("TambahTransaksiActivity", "Response Code: ${response.code()}")
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@TambahTransaksiActivity,
                            responseBody?.message ?: "Transaksi berhasil ditambahkan",
                            Toast.LENGTH_LONG
                        ).show()
                        showTransactionSuccessDialog(responseBody?.transaction?.change ?: 0)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        val errorBody = response.errorBody()?.string()
                        Log.e("TambahTransaksiActivity", "Error: $errorBody")
                        Toast.makeText(
                            this@TambahTransaksiActivity,
                            "Gagal menambahkan transaksi: $errorBody",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@TambahTransaksiActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("Errornya", e.message.toString())
                }
            }
        }
    }

    private fun showTransactionSuccessDialog(change: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_transaction_success, null)
        val changeTextView = dialogView.findViewById<TextView>(R.id.changeTextView)
        val okButton = dialogView.findViewById<Button>(R.id.okButton)

        changeTextView.text = "Kembalian: $change"

        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        okButton.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        dialog.show()
    }
}
