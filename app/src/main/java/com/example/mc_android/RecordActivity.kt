package com.example.mc_android

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mc_android.databinding.ActivityRecordBinding
import com.example.mc_android.mydata.MyData
import com.example.mc_android.services.DateTimeUtils

class RecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedItem: MyData? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("selectedItem", MyData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("selectedItem")
        }

        selectedItem?.let { item ->
            binding.StartDate.text = DateTimeUtils(item.startAt).convertToLocalDate()
            binding.StartTime.text = "시작: ${DateTimeUtils(item.startAt).convertToLocalTime()}"
            binding.EndTime.text = "종료: ${DateTimeUtils(item.startAt).convertToLocalTime()}"
            binding.time.text = "${String.format("%.1f", (item.time / 3600.0))}시간"
            binding.distance.text = item.distance.toString();
            binding.totalElevation.text = "${item.totalElevation}m"
            binding.avgFace.text = "${item.avgFace}"
            binding.weather.text = "온도: ${item.temperature}°C\n습도: ${item.humidity}"
        }
    }
}
