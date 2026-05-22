package com.example.truyvetyte

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MedicalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Chỉ set layout để hiển thị giao diện tĩnh
        setContentView(R.layout.medical_activity_dashboard)

        // 2. Lấy tên cán bộ từ Login truyền sang (để giao diện trông thực tế hơn)
        val hoTen = intent.getStringExtra("HoTen") ?: "Cán bộ y tế"
        val tvTitleHeader = findViewById<TextView>(R.id.tvTitleHeader)
        tvTitleHeader.text = "Xin chào, $hoTen"

        // 3. Xử lý nút đăng xuất để bạn tiện test quay lại màn hình Login
        val btnLogout = findViewById<ImageButton>(R.id.iv_logout_header)
        btnLogout.setOnClickListener {
            // Xóa phiên đăng nhập
            val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()

            Toast.makeText(this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show()

            // Chuyển về màn hình đăng nhập
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}