package com.example.aplikasicoffeeshop

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Splashscreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splashscreen)

        // Setelah 2 detik, arahkan ke LoginActivity
        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Menutup Splashscreen agar tidak bisa kembali
        }, 2000)
    }
}