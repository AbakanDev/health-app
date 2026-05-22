package com.example.truyvetyte

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.truyvetyte.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LichSuTruyVet : Fragment() {

    private lateinit var tvF0Count: TextView
    private lateinit var tvF1Count: TextView
    private lateinit var tvF2Count: TextView
    private lateinit var tvTotalContactCount: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.contact_tracing_screen, container, false)

        // 1. Ánh xạ View với layout XML
        tvF0Count = view.findViewById(R.id.tvF0Count)
        tvF1Count = view.findViewById(R.id.tvF1Count)
        tvF2Count = view.findViewById(R.id.tvF2Count)
        tvTotalContactCount = view.findViewById(R.id.tvTotalContactCount)

        // 2. Gọi hàm lấy dữ liệu
        fetchContactStats()

        return view
    }

    private fun fetchContactStats() {
        // Đồng bộ cách gọi SharedPreferences giống hệt DichTe.kt
        val sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("TOKEN", null)
        val cccd = sharedPreferences.getString("CCCD", null)

        if (token == null || cccd == null) {
            Toast.makeText(requireContext(), "Lỗi: Không tìm thấy Token hoặc CCCD. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show()
            return
        }

        // Định dạng lại token để gửi lên Header
        val bearerToken = "Bearer $token"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Gọi API qua RetrofitClient
                val response = RetrofitClient.instance.getContactStats(bearerToken, cccd)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null && body.success) {
                            // Cập nhật UI nếu lấy data thành công
                            body.data?.let { stats ->
                                tvF0Count.text = stats.SoLuotF0
                                tvF1Count.text = stats.SoLuotF1
                                tvF2Count.text = stats.SoLuotF2
                                tvTotalContactCount.text = stats.TongLuotTiepXuc.toString()
                            }
                        } else {
                            // Nếu API trả về success = false
                            Toast.makeText(requireContext(), body?.message ?: "Chưa có dữ liệu tiếp xúc", Toast.LENGTH_SHORT).show()

                            // Gán mặc định bằng 0 nếu chưa tiếp xúc ai
                            tvF0Count.text = "0"
                            tvF1Count.text = "0"
                            tvF2Count.text = "0"
                            tvTotalContactCount.text = "0"
                        }
                    } else {
                        Toast.makeText(requireContext(), "Lỗi Server: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("LichSuTruyVet", "Lỗi kết nối: ${e.message}")
                    Toast.makeText(requireContext(), "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}