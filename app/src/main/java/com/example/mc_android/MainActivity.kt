package com.example.mc_android

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var backPressedTime: Long = 0 // 뒤로 가기 버튼이 눌린 시간을 기록할 변수

    override fun attachBaseContext(newBase: Context) {
        val locale = Locale("ko")
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // 위치정보 권한 요청
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)

        // 체중값이 없는 경우 체중 입력 요청
        getSharedPreferences("PREF", Context.MODE_PRIVATE).also {
            if(it.getFloat("WEIGHT", -1.0f) == -1.0f) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("체중을 입력하세요")

                val input = EditText(this)
                input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                builder.setView(input)

                builder.setPositiveButton("저장") { dialog, which ->
                    try {
                        val weight = input.text.toString().toDouble()
                        it.edit().putFloat("WEIGHT", weight.toFloat()).apply()
                        Toast.makeText(this, "저장되었습니다", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, "잘못된 입력입니다", Toast.LENGTH_SHORT).show()
                    }
                }
                builder.setNegativeButton("취소") { dialog, which -> dialog.cancel() }

                builder.show()
            }
        }

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)

        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 3

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> MainFragment()
//                    1 -> RecordFragment()
                    1 -> MeasureFragment()
                    2 -> ChatbotFragment()
                    else -> MainFragment()
                }
            }
        }

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Main"
//                1 -> "Record"
                1 -> "Measure"
                2 -> "Chatbot"
                else -> "Main"
            }
        }.attach()

    }

    override fun onBackPressed() {
        // 현재 시간에서 마지막으로 뒤로가기 버튼을 누른 시간을 빼서 2초가 넘었는지 확인
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            return
        } else {
            // 2초가 넘지 않았다면 토스트 메시지로 한 번 더 누르면 종료된다고 알림
            Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}
