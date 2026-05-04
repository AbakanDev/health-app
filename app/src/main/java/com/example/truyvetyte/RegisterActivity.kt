package com.example.truyvetyte

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    // Khởi tạo OkHttpClient
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in_screen)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        val edtCccd = findViewById<AppCompatEditText>(R.id.edtCccd)
        val edtPassword = findViewById<AppCompatEditText>(R.id.edtPassword)
        val edtConfirmPassword = findViewById<AppCompatEditText>(R.id.edtConfirmPassword)
        val btnRegisterSubmit = findViewById<AppCompatButton>(R.id.btnRegisterSubmit)

        btnRegisterSubmit.setOnClickListener {
            val cccd = edtCccd.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val confirmPassword = edtConfirmPassword.text.toString().trim()

            // 1. Validate form
            if (cccd.length != 12) {
                edtCccd.error = "CCCD phải bao gồm đúng 12 số"
                edtCccd.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                edtPassword.error = "Vui lòng nhập mật khẩu"
                edtPassword.requestFocus()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                edtConfirmPassword.error = "Mật khẩu xác nhận không khớp"
                edtConfirmPassword.requestFocus()
                return@setOnClickListener
            }

            // 2. Gói dữ liệu thành JSON
            val jsonObject = JSONObject()
            jsonObject.put("cccd", cccd)
            jsonObject.put("password", password)
            val jsonString = jsonObject.toString()

            val requestBody = jsonString.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            // SỬ DỤNG CONSTANTS THAY VÌ LINK CỨNG
            val url = Constants.REGISTER_URL

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            // Disable nút để tránh spam click khi đang tải
            btnRegisterSubmit.isEnabled = false
            btnRegisterSubmit.text = "ĐANG XỬ LÝ..."

            // 3. Gửi Request lên Node.js
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        btnRegisterSubmit.isEnabled = true
                        btnRegisterSubmit.text = "ĐĂNG KÝ"
                        Toast.makeText(this@RegisterActivity, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()

                    runOnUiThread {
                        btnRegisterSubmit.isEnabled = true
                        btnRegisterSubmit.text = "ĐĂNG KÝ"

                        if (response.isSuccessful && responseData != null) {
                            val jsonRes = JSONObject(responseData)
                            val isSuccess = jsonRes.getBoolean("success")
                            val message = jsonRes.getString("message")

                            if (isSuccess) {
                                Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
                                // Đăng ký xong tự động quay về trang Login
                                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                edtCccd.error = message
                                edtCccd.requestFocus()
                            }
                        } else {
                            // Trường hợp server trả về lỗi 400 (Trùng CCCD)
                            if(responseData != null){
                                val jsonRes = JSONObject(responseData)
                                val message = jsonRes.getString("message")
                                edtCccd.error = message
                                edtCccd.requestFocus()
                            } else {
                                Toast.makeText(this@RegisterActivity, "Đăng ký thất bại", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            })
        }
    }
}