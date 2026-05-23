package com.example.truyvetyte

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.truyvetyte.model.ImmigrationHistoryItem
import com.example.truyvetyte.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KhaiBao2 : Fragment() {

    private lateinit var tvChuaXuatNhapCanh: TextView
    private lateinit var layoutXuatNhapCanhContainer: LinearLayout
    private lateinit var btnHoanTat2: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.initial_immigration_declaration_screen,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ánh xạ view
        tvChuaXuatNhapCanh = view.findViewById(R.id.tvChuaXuatNhapCanh)
        layoutXuatNhapCanhContainer =
            view.findViewById(R.id.layoutXuatNhapCanhContainer)
        btnHoanTat2 = view.findViewById(R.id.btnHoanTat2)

        // Nút hoàn tất
        btnHoanTat2.setOnClickListener {
            val intent = Intent(requireContext(), ChucMungKhaiBao::class.java)
            startActivity(intent)
        }

        // Load lịch sử xuất nhập cảnh
        tvChuaXuatNhapCanh.visibility = View.GONE
        fetchImmigrationHistory()
    }

    // ─── LỊCH SỬ XUẤT NHẬP CẢNH ───────────────────────────────────────────────

    private fun fetchImmigrationHistory() {

        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

        val token = sharedPreferences.getString("TOKEN", null)
        val cccd = sharedPreferences.getString("CCCD", null)

        if (token == null || cccd == null) {
            tvChuaXuatNhapCanh.visibility = View.VISIBLE
            tvChuaXuatNhapCanh.text =
                "Không tìm thấy thông tin người dùng"
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {

            try {

                val response = RetrofitClient.instance
                    .getImmigrationHistory("Bearer $token", cccd)

                withContext(Dispatchers.Main) {

                    if (response.isSuccessful) {

                        val body = response.body()

                        if (
                            body != null &&
                            body.success &&
                            !body.data.isNullOrEmpty()
                        ) {

                            tvChuaXuatNhapCanh.visibility = View.GONE
                            layoutXuatNhapCanhContainer.removeAllViews()

                            body.data.forEachIndexed { index, item ->

                                // Divider
                                if (index > 0) {

                                    val divider = View(requireContext()).apply {
                                        layoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            1
                                        ).also { p ->
                                            p.marginStart = 16.dpToPx()
                                            p.marginEnd = 16.dpToPx()
                                        }

                                        setBackgroundColor(0xFFE0E0E0.toInt())
                                    }

                                    layoutXuatNhapCanhContainer.addView(divider)
                                }

                                // Row
                                val row = RelativeLayout(requireContext()).apply {

                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )

                                    setPadding(
                                        16.dpToPx(),
                                        12.dpToPx(),
                                        16.dpToPx(),
                                        12.dpToPx()
                                    )
                                }

                                // Tên cửa khẩu
                                val tvCuaKhau = TextView(requireContext()).apply {

                                    id = View.generateViewId()

                                    text =
                                        "📍 ${item.tenCuaKhau} (${item.loaiCuaKhau})"

                                    textSize = 18f

                                    setTextColor(0xFF79A9F5.toInt())

                                    setTypeface(typeface, Typeface.BOLD)

                                    layoutParams = RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT
                                    ).also { p ->
                                        p.addRule(RelativeLayout.ALIGN_PARENT_START)
                                    }
                                }

                                // Thời gian
                                val tvThoiGian = TextView(requireContext()).apply {

                                    id = View.generateViewId()

                                    text = "${item.ngay} - ${item.gio}"

                                    textSize = 14f

                                    setTextColor(0xFF92BEFA.toInt())

                                    setTypeface(typeface, Typeface.BOLD)

                                    layoutParams = RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT
                                    ).also { p ->
                                        p.addRule(RelativeLayout.BELOW, tvCuaKhau.id)
                                        p.topMargin = 6.dpToPx()
                                    }
                                }

                                // Trạng thái
                                val tvTrangThai = TextView(requireContext()).apply {

                                    text =
                                        "Trạng thái: ${item.trangThaiCuaKhau}"

                                    textSize = 14f

                                    setTextColor(0xFF666666.toInt())

                                    layoutParams = RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT
                                    ).also { p ->
                                        p.addRule(RelativeLayout.ALIGN_PARENT_END)
                                        p.addRule(RelativeLayout.CENTER_VERTICAL)
                                    }
                                }

                                row.addView(tvCuaKhau)
                                row.addView(tvThoiGian)
                                row.addView(tvTrangThai)

                                layoutXuatNhapCanhContainer.addView(row)
                            }

                        } else {

                            tvChuaXuatNhapCanh.visibility = View.VISIBLE
                            layoutXuatNhapCanhContainer.removeAllViews()

                            tvChuaXuatNhapCanh.text =
                                "Bạn chưa có lịch sử xuất nhập cảnh"
                        }

                    } else {

                        Toast.makeText(
                            requireContext(),
                            "Không thể tải dữ liệu",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {

                Log.e("KhaiBao2", "Lỗi lịch sử xuất nhập cảnh: ${e.message}")

                withContext(Dispatchers.Main) {

                    Toast.makeText(
                        requireContext(),
                        "Lỗi kết nối mạng",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // ─── DP TO PX ─────────────────────────────────────────────────────────────

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}