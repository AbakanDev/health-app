package com.example.truyvetyte

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class LoginActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.login_screen)

        val btnGo = findViewById<Button>(/* id = */ R.id.btn_register)
        btnGo.setOnClickListener {
            // Chuyển từ trang hiện tại (this) sang FriendActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}