package com.example.aplikasicoffeeshop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasicoffeeshop.api.ApiConfig
import com.example.aplikasicoffeeshop.api.ApiService
import com.example.aplikasicoffeeshop.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var username: EditText
    lateinit var password: EditText
    lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)
        if (token!=null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener(View.OnClickListener {
            val usernameText = username.text.toString()
            val passwordText = password.text.toString()

            if(usernameText.isNotEmpty() && passwordText.isNotEmpty()){
                login(usernameText, passwordText)
            } else {
                Toast.makeText(this, "Harap isi username dan password", Toast.LENGTH_SHORT).show()
            }


        })
    }
    private fun login(username: String, password: String) {
        val retrofit = ApiConfig().getRetrofitClientInstance()
        val apiService = retrofit.create(ApiService::class.java)
        apiService.login(username, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()
                    if (loginResponse?.user != null && loginResponse.token != null) {
                        val Name = loginResponse.user.name ?: "Unknown"
                        val token = loginResponse.token
                        val sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("auth_token", token)
                        editor.putString("customer_name", Name)
                        editor.apply()
                        Toast.makeText(
                            this@LoginActivity,
                            "Login berhasil! Welcome $Name",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Username atau password salah", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Gagal terhubung ke server.", Toast.LENGTH_LONG).show()
                Log.e("LoginError", "Error: ${t.message}")
            }
        })
    }
}