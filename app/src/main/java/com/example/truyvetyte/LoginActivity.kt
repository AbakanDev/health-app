package com.example.truyvetyte

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.truyvetyte.network.RetrofitClient
import com.example.truyvetyte.model.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)
        val btnGo = findViewById<Button>(R.id.btn_register)
        btnGo.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // --- CODE MỚI DÀNH CHO ĐĂNG NHẬP BẰNG RETROFIT ---
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val etUsername = findViewById<EditText>(R.id.et_username) // Nhập CCCD
        val etPassword = findViewById<EditText>(R.id.et_password) // Nhập Mật khẩu

        btnLogin.setOnClickListener {
            val cccd = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // 1. Kiểm tra rỗng
            if (cccd.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ CCCD và mật khẩu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Gọi API Login qua Retrofit
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // Tạo cục dữ liệu Request
                    val request = LoginRequest(cccd, password)
                    // Gọi API
                    val response = RetrofitClient.instance.loginUser(request)

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful && response.body() != null) {
                            // Thành công (HTTP 200)
                            val body = response.body()
                            if (body?.status == "success") {
                                Toast.makeText(this@LoginActivity, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                                // Lấy tên, nếu null thì để là "Bạn"
                                val fullName = body.data?.fullName ?: "Bạn"

                                // Chuyển trang và truyền tên qua MainActivity
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.putExtra("USER_NAME", fullName)
                                startActivity(intent)
                                finish() // Đóng màn hình login
                            }
                        } else {
                            // Backend trả về HTTP 400, 401, 404 (Sai pass, sai cccd...)
                            // Đọc lỗi từ errorBody do Retrofit phân loại
                            val errorString = response.errorBody()?.string()
                            if (errorString != null) {
                                val jsonObject = JSONObject(errorString)
                                val message = jsonObject.getString("message")
                                Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this@LoginActivity, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Lỗi rớt mạng hoặc sập server
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Lỗi kết nối: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}