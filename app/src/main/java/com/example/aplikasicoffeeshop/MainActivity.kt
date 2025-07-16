package com.example.aplikasicoffeeshop

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import com.example.aplikasicoffeeshop.api.ApiConfig
import com.example.aplikasicoffeeshop.api.ApiService
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val token: String by lazy {
        getSharedPreferences("user", MODE_PRIVATE).getString("auth_token", "") ?: ""
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavBarMain)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.navigation_transaksi -> {
                    selectedFragment = TransaksiFragment()
                }
                R.id.navigation_produk -> {
                    selectedFragment = ProdukFragment()
                }
                R.id.navigation_logout -> {
                    AlertDialog.Builder(this).apply {
                        setTitle("Konfirmasi Logout")
                        setMessage("Apakah Anda yakin ingin logout?")
                        setPositiveButton("Logout") { dialog, _ ->
                            logout()
                            dialog.dismiss()
                        }
                        setNegativeButton("Batal") { dialog, _ ->
                            dialog.dismiss()
                        }
                        create()
                        show()
                    }
                }
            }
            selectedFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerMain, it)
                    .commit()
            }
            true
        }

        // Set default selection
        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.navigation_transaksi
        }

    }
    private fun logout() {
        val retrofit = ApiConfig().getRetrofitClientInstance()
        val apiService = retrofit.create(ApiService::class.java)
        apiService.logout("Bearer $token").enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                    val sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.remove("auth_token")
                    editor.apply()
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@MainActivity, "Respon gagal", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
