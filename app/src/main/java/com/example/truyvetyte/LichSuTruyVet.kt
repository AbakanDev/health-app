package com.example.truyvetyte

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.truyvetyte.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LichSuTruyVet : Fragment() {

    // Views cho Tổng Quan
    private lateinit var tvF0Count: TextView
    private lateinit var tvF1Count: TextView
    private lateinit var tvF2Count: TextView
    private lateinit var tvTotalContactCount: TextView

    // Views cho Lịch Sử Tiếp Xúc - Item 1
    private lateinit var cvContactItem1: CardView
    private lateinit var tvContactLevel1: TextView
    private lateinit var tvContactLocation1: TextView
    private lateinit var tvContactTime1: TextView

    // Views cho Lịch Sử Tiếp Xúc - Item 2
    private lateinit var cvContactItem2: CardView
    private lateinit var tvContactLevel2: TextView
    private lateinit var tvContactLocation2: TextView
    private lateinit var tvContactTime2: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.contact_tracing_screen, container, false)

        // 1. Ánh xạ View Tổng Quan
        tvF0Count = view.findViewById(R.id.tvF0Count)
        tvF1Count = view.findViewById(R.id.tvF1Count)
        tvF2Count = view.findViewById(R.id.tvF2Count)
        tvTotalContactCount = view.findViewById(R.id.tvTotalContactCount)

        // 2. Ánh xạ View Lịch Sử Tiếp Xúc
        cvContactItem1 = view.findViewById(R.id.cvContactItem1)
        tvContactLevel1 = view.findViewById(R.id.tvContactLevel1)
        tvContactLocation1 = view.findViewById(R.id.tvContactLocation1)
        tvContactTime1 = view.findViewById(R.id.tvContactTime1)

        cvContactItem2 = view.findViewById(R.id.cvContactItem2)
        tvContactLevel2 = view.findViewById(R.id.tvContactLevel2)
        tvContactLocation2 = view.findViewById(R.id.tvContactLocation2)
        tvContactTime2 = view.findViewById(R.id.tvContactTime2)

        // Ẩn mặc định các item lịch sử tiếp xúc, khi nào có data mới hiện
        cvContactItem1.visibility = View.GONE
        cvContactItem2.visibility = View.GONE

        // 3. Gọi API
        fetchContactStats()
        fetchContactHistory() // Gọi thêm API lấy lịch sử

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

    private fun fetchContactHistory() {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("TOKEN", null)
        val cccd = sharedPreferences.getString("CCCD", null)

        if (token == null || cccd == null) return

        val bearerToken = "Bearer $token"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getContactHistory(bearerToken, cccd)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null && body.success && !body.data.isNullOrEmpty()) {
                            val listData = body.data

                            // Gán dữ liệu cho Item 1 (nếu có)
                            if (listData.isNotEmpty()) {
                                cvContactItem1.visibility = View.VISIBLE
                                tvContactLevel1.text = listData[0].capDoDichTeHienTai ?: "An Toàn"
                                tvContactLocation1.text = listData[0].diaDiemTiepXuc ?: "Không rõ"
                                tvContactTime1.text = listData[0].thoiGianTiepXuc ?: "Không rõ"
                            }

                            // Gán dữ liệu cho Item 2 (nếu có)
                            if (listData.size > 1) {
                                cvContactItem2.visibility = View.VISIBLE
                                tvContactLevel2.text = listData[1].capDoDichTeHienTai ?: "An Toàn"
                                tvContactLocation2.text = listData[1].diaDiemTiepXuc ?: "Không rõ"
                                tvContactTime2.text = listData[1].thoiGianTiepXuc ?: "Không rõ"
                            }
                        }
                    } else {
                        Log.e("LichSuTruyVet", "Lỗi lấy lịch sử tiếp xúc: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("LichSuTruyVet", "Lỗi kết nối lịch sử tiếp xúc: ${e.message}")
                }
            }
        }
    }
}