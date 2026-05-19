package com.example.truyvetyte

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment

class KhaiBao : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Dòng này cực kỳ quan trọng: Kết nối với layout bạn của bạn đã làm
        return inflater.inflate(R.layout.initial_health_declaration_screen, container, false)
    }
    // Trong file XuHuongFragment.kt (hoặc Trang1Fragment.kt)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Tìm nút bấm trong layout của Fragment (Trang 1)
        // Giả sử nút đó có ID là btn_go_to_page2
        val btnHoanTat = view.findViewById<Button>(R.id.btnHoanTat)

        // 2. Thiết lập click
        btnHoanTat.setOnClickListener {
            // 1. Lấy FragmentManager từ Fragment hiện tại
            val fragmentManager = parentFragmentManager
            val transaction = fragmentManager.beginTransaction()

            // 2. Thay thế Fragment hiện tại bằng KhaiBao2
            // R.id.main_content_frame là cái ID của FrameLayout trong activity_main.xml
            transaction.replace(R.id.main_content_frame, KhaiBao2())

            // 3. (Tùy chọn) Thêm vào BackStack để khi nhấn nút Back của điện thoại
            // nó sẽ quay lại Trang 1 thay vì thoát app
            transaction.addToBackStack(null)

            // 4. Hoàn tất việc chuyển đổi
            transaction.commit()
        }
    }
}

