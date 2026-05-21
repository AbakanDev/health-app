package com.example.truyvetyte

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class TrendAnalysisActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Đảm bảo tên layout khớp với tên file XML của bạn (ví dụ: activity_trend_analysis)
        setContentView(R.layout.dashboard_screen)

        // 1. Ánh xạ BarChart
        val barChart = findViewById<BarChart>(R.id.barChart)
        setupBarChart(barChart)

        // 2. Ánh xạ thử một vài TextView khác để chuẩn bị cho việc đổ data thật sau này
        val tvStatF0 = findViewById<TextView>(R.id.tv_stat_f0)
        val tvStatF1F2 = findViewById<TextView>(R.id.tv_stat_f1_f2)
        val tvVaccineDose1 = findViewById<TextView>(R.id.tv_vaccine_dose_1)
        val tvStatusMain = findViewById<TextView>(R.id.tv_status_main)

        // Ví dụ cách set dữ liệu text:
        // tvStatF0.text = "20.150"
        // tvStatusMain.text = "Nguy Cơ Cao"
        // tvStatusMain.setTextColor(Color.parseColor("#F44336")) // Đổi màu thành đỏ
    }

    private fun setupBarChart(barChart: BarChart) {
        // 1. Khởi tạo mảng chứa dữ liệu của 12 tháng (Trục X tính từ 0 đến 11)
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, 1200f))  // Tháng 1
        entries.add(BarEntry(1f, 1500f))  // Tháng 2
        entries.add(BarEntry(2f, 2800f))  // Tháng 3
        entries.add(BarEntry(3f, 3200f))  // Tháng 4
        entries.add(BarEntry(4f, 2100f))  // Tháng 5
        entries.add(BarEntry(5f, 4500f))  // Tháng 6
        entries.add(BarEntry(6f, 5000f))  // Tháng 7
        entries.add(BarEntry(7f, 3800f))  // Tháng 8
        entries.add(BarEntry(8f, 2900f))  // Tháng 9
        entries.add(BarEntry(9f, 1500f))  // Tháng 10
        entries.add(BarEntry(10f, 900f))  // Tháng 11
        entries.add(BarEntry(11f, 400f))  // Tháng 12

        // 2. Cấu hình màu sắc và style cho cột
        val dataSet = BarDataSet(entries, "Số ca nhiễm (F0)")
        dataSet.color = Color.parseColor("#5C94F0") // Màu xanh dương giống thiết kế UI
        dataSet.valueTextColor = Color.parseColor("#5C94F0")
        dataSet.valueTextSize = 10f

        // Định dạng số trên đầu cột (bỏ số thập phân .0)
        dataSet.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        // 3. Đưa dataset vào BarData và truyền cho biểu đồ
        val barData = BarData(dataSet)
        // Điều chỉnh độ rộng của cột (mặc định là 0.85f)
        barData.barWidth = 0.6f
        barChart.data = barData

        // 4. Tuỳ chỉnh trục X (Nằm ngang)
        val months = arrayOf("T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12")
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(months)
        xAxis.position = XAxis.XAxisPosition.BOTTOM // Chữ nằm dưới chân cột
        xAxis.setDrawGridLines(false) // Tắt lưới dọc
        xAxis.granularity = 1f // Bước nhảy trục X là 1 (để không bị bỏ cóc nhãn)
        xAxis.textColor = Color.parseColor("#4DB6B0") // Màu chữ trục X

        // 5. Tuỳ chỉnh trục Y (Nằm dọc)
        barChart.axisRight.isEnabled = false // Tắt trục Y bên phải

        val leftAxis = barChart.axisLeft
        leftAxis.setDrawGridLines(true) // Bật lưới ngang mờ mờ
        leftAxis.gridColor = Color.parseColor("#E0E0E0")
        leftAxis.axisMinimum = 0f // Bắt đầu từ 0
        leftAxis.textColor = Color.parseColor("#90CAF9") // Màu chữ trục Y

        // 6. Các cài đặt chung cho biểu đồ
        barChart.description.isEnabled = false // Ẩn chữ "Description Label"
        barChart.legend.isEnabled = false // Ẩn chú thích "Số ca nhiễm (F0)" ở dưới cùng cho đỡ chật
        barChart.setFitBars(true)
        barChart.animateY(1000) // Hiệu ứng mọc cột lên trong 1 giây

        // 7. Làm mới biểu đồ
        barChart.invalidate()
    }
}