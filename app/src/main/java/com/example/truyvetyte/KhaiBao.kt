package com.example.truyvetyte

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.truyvetyte.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KhaiBao : Fragment() {

    // Khai báo các view từ XML
    private lateinit var tvChuaKhaiBao: TextView
    private lateinit var layoutLan1: RelativeLayout
    private lateinit var layoutLan2: RelativeLayout
    private lateinit var tvNgayKhaiBaoLan1: TextView
    private lateinit var tvNgayKhaiBaoLan2: TextView
    private lateinit var dividerKhaiBao: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.initial_health_declaration_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Ánh xạ các View phần lịch sử
        tvChuaKhaiBao = view.findViewById(R.id.tvChuaKhaiBao)
        layoutLan1 = view.findViewById(R.id.layoutLan1)
        layoutLan2 = view.findViewById(R.id.layoutLan2)
        tvNgayKhaiBaoLan1 = view.findViewById(R.id.tvNgayKhaiBaoLan1)
        tvNgayKhaiBaoLan2 = view.findViewById(R.id.tvNgayKhaiBaoLan2)
        dividerKhaiBao = view.findViewById(R.id.dividerKhaiBao)

        // Ẩn lịch sử mặc định cho đến khi API trả về
        layoutLan1.visibility = View.GONE
        layoutLan2.visibility = View.GONE
        dividerKhaiBao.visibility = View.GONE

        // 2. Chức năng nút Hoàn tất
        val btnGo = view.findViewById<Button>(R.id.btnHoanTat1)
        btnGo.setOnClickListener {
            val intent = Intent(requireContext(), ChucMungKhaiBao::class.java)
            startActivity(intent)
        }

        // 3. Gọi API lấy lịch sử khai báo
        fetchKhaiBaoHistory()
    }

    private fun fetchKhaiBaoHistory() {
        // Lấy token và cccd từ SharedPreferences giống màn hình Lịch Sử Truy Vết
        val sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("TOKEN", null)
        val cccd = sharedPreferences.getString("CCCD", null)

        if (token == null || cccd == null) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show()
            return
        }

        val bearerToken = "Bearer $token"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Gọi API
                val response = RetrofitClient.instance.getKhaiBaoHistory(bearerToken, cccd)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null && body.success && !body.data.isNullOrEmpty()) {
                            val listData = body.data

                            // Ẩn thông báo "Chưa khai báo"
                            tvChuaKhaiBao.visibility = View.GONE

                            // Dựa theo câu query SQL của bạn, data trả về đã xếp DESC (mới nhất xếp trước)
                            // Map Item mới nhất (Index 0) vào layoutLan2 (XML của bạn thiết kế Lan2 ở trên Lan1)
                            if (listData.isNotEmpty()) {
                                layoutLan2.visibility = View.VISIBLE
                                tvNgayKhaiBaoLan2.text = "${listData[0].Ngay} - ${listData[0].Gio}"
                            }

                            // Map Item cũ hơn (Index 1) vào layoutLan1 nếu có
                            if (listData.size > 1) {
                                dividerKhaiBao.visibility = View.VISIBLE
                                layoutLan1.visibility = View.VISIBLE
                                tvNgayKhaiBaoLan1.text = "${listData[1].Ngay} - ${listData[1].Gio}"
                            }

                        } else {
                            // Không có data -> Hiện chữ "Bạn chưa có lịch sử khai báo"
                            tvChuaKhaiBao.visibility = View.VISIBLE
                            layoutLan1.visibility = View.GONE
                            layoutLan2.visibility = View.GONE
                            dividerKhaiBao.visibility = View.GONE
                        }
                    } else {
                        Log.e("KhaiBao", "Lỗi Server: ${response.code()}")
                        tvChuaKhaiBao.visibility = View.VISIBLE
                        tvChuaKhaiBao.text = "Không thể tải lịch sử khai báo"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("KhaiBao", "Lỗi kết nối: ${e.message}")
                    tvChuaKhaiBao.visibility = View.VISIBLE
                    tvChuaKhaiBao.text = "Lỗi kết nối mạng"
                }
            }
        }
    }
}