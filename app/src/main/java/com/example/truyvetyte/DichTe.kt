package com.example.truyvetyte

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.truyvetyte.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DichTe : Fragment() {

    // Khai báo các view cần tương tác
    private lateinit var cvTheTiemChung: CardView
    private lateinit var tvLoaiThe: TextView
    private lateinit var tvSoMuiTiem: TextView
    private lateinit var tvChuaTiemChung: TextView

    private lateinit var layoutMui1: RelativeLayout
    private lateinit var tvTenVaccineMui1: TextView
    private lateinit var tvDiaDiemMui1: TextView
    private lateinit var tvNgayTiemMui1: TextView

    private lateinit var layoutMui2: RelativeLayout
    private lateinit var tvTenVaccineMui2: TextView
    private lateinit var tvDiaDiemMui2: TextView
    private lateinit var tvNgayTiemMui2: TextView

    private lateinit var dividerTiemChung: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.public_health_management_screen, container, false)

        // Ánh xạ View
        initViews(view)

        // Gọi API lấy dữ liệu
        fetchTiemChungData()

        return view
    }

    private fun initViews(view: View) {
        cvTheTiemChung = view.findViewById(R.id.cvTheTiemChung)
        tvLoaiThe = view.findViewById(R.id.tvLoaiThe)
        tvSoMuiTiem = view.findViewById(R.id.tvSoMuiTiem)
        tvChuaTiemChung = view.findViewById(R.id.tvChuaTiemChung)

        layoutMui1 = view.findViewById(R.id.layoutMui1)
        tvTenVaccineMui1 = view.findViewById(R.id.tvTenVaccineMui1)
        tvDiaDiemMui1 = view.findViewById(R.id.tvDiaDiemMui1)
        tvNgayTiemMui1 = view.findViewById(R.id.tvNgayTiemMui1)

        layoutMui2 = view.findViewById(R.id.layoutMui2)
        tvTenVaccineMui2 = view.findViewById(R.id.tvTenVaccineMui2)
        tvDiaDiemMui2 = view.findViewById(R.id.tvDiaDiemMui2)
        tvNgayTiemMui2 = view.findViewById(R.id.tvNgayTiemMui2)

        dividerTiemChung = view.findViewById(R.id.dividerTiemChung)
    }

    private fun fetchTiemChungData() {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("TOKEN", null)
        val cccd = sharedPreferences.getString("CCCD", null)

        if (token == null || cccd == null) {
            Toast.makeText(requireContext(), "Lỗi: Không tìm thấy Token hoặc CCCD. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show()
            return
        }

        // Tạo bearer token
        val bearerToken = "Bearer $token"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getThongTinTiemChung(bearerToken, cccd)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!
                        updateUI(data.soMuiTiem, data.loaiThe, data.danhSachTiem)
                    } else {
                        Toast.makeText(requireContext(), "Không lấy được dữ liệu tiêm chủng!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUI(soMuiTiem: Int, loaiThe: String, danhSach: List<com.example.truyvetyte.model.ChiTietTiem>?) {
        // 1. Cập nhật Thẻ chứng nhận (Màu sắc, chữ)
        tvSoMuiTiem.text = soMuiTiem.toString()
        when (loaiThe.uppercase()) {
            "XANH" -> {
                cvTheTiemChung.setCardBackgroundColor(Color.parseColor("#4CAF50"))
                tvLoaiThe.text = "THẺ XANH"
            }
            "VANG" -> {
                cvTheTiemChung.setCardBackgroundColor(Color.parseColor("#FFC107"))
                tvLoaiThe.text = "THẺ VÀNG"
            }
            "DO" -> {
                cvTheTiemChung.setCardBackgroundColor(Color.parseColor("#F44336"))
                tvLoaiThe.text = "THẺ ĐỎ"
            }
        }

        // 2. Reset trạng thái UI danh sách mũi tiêm
        layoutMui1.visibility = View.GONE
        layoutMui2.visibility = View.GONE
        dividerTiemChung.visibility = View.GONE
        tvChuaTiemChung.visibility = View.GONE

        // 3. Cập nhật danh sách mũi tiêm
        if (soMuiTiem == 0 || danhSach.isNullOrEmpty()) {
            tvChuaTiemChung.visibility = View.VISIBLE
        } else {
            danhSach.forEach { muiTiem ->
                if (muiTiem.muiSo == 1) {
                    layoutMui1.visibility = View.VISIBLE
                    tvTenVaccineMui1.text = muiTiem.tenVaccine
                    tvDiaDiemMui1.text = muiTiem.diaDiem
                    tvNgayTiemMui1.text = muiTiem.ngayTiem
                } else if (muiTiem.muiSo == 2) {
                    layoutMui2.visibility = View.VISIBLE
                    dividerTiemChung.visibility = View.VISIBLE
                    tvTenVaccineMui2.text = muiTiem.tenVaccine
                    tvDiaDiemMui2.text = muiTiem.diaDiem
                    tvNgayTiemMui2.text = muiTiem.ngayTiem
                }
            }
        }
    }
}