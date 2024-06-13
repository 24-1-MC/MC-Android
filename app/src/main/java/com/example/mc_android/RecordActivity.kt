package com.example.mc_android

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
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

        // 기록 정보 받아오기
        selectedItem?.let { item ->
            binding.dateHistory.text = DateTimeUtils(item.startAt).convertToLocalDate()
            binding.timeHistory.text = "시작: ${DateTimeUtils(item.startAt).convertToLocalTime()}   종료: ${DateTimeUtils(item.endAt).convertToLocalTime()}"
            binding.time.text = String.format("%d:%02d:%02d", item.time/3600, item.time/60, item.time%60)
            binding.distance.text = String.format("%.2fkm", item.distance)
            binding.totalElevation.text = "${item.totalElevation.toInt()}m"
            binding.avgPace.text = String.format("%d'%02d''", (item.avgPace/60).toInt(), (item.avgPace%60).toInt())
            val resourceId = resources.getIdentifier("weather_ic_${item.weatherIcon}", "drawable", packageName)
            binding.weatherIcon.setImageResource(resourceId)
            binding.weather.text = String.format("온도: %d°\n습도: %d%%", item.temperature, item.humidity)//"온도: ${item.temperature}°C\n습도: ${item.humidity}"
        }

        // 맵 버튼 연결
        binding.map.setOnClickListener {
            val intent = Intent(applicationContext, MapView::class.java)
            intent.putExtra("fileName", selectedItem?.locationFileName.toString())
            Log.d("DEBUG", "throw " + selectedItem?.locationFileName.toString())
            startActivity(intent)
        }
    }
}
