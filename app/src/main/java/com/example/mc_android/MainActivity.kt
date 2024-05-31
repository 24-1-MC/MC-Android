package com.example.mc_android

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.mc_android.com.example.mc_android.MainFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 위치정보 권한 요청
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)

        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 4

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> MainFragment()
                    1 -> RecordFragment()
                    2 -> MeasureFragment()
                    3 -> ChatbotFragment()
                    else -> MainFragment()
                }
            }
        }

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Main"
                1 -> "Record"
                2 -> "Measure"
                3 -> "Chatbot"
                else -> "Main"
            }
        }.attach()

    }
}
