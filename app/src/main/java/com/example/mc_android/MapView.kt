package com.example.mc_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import com.example.mc_android.databinding.MapViewBinding
import com.google.android.material.animation.DrawableAlphaProperty
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class MapView: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = MapViewBinding.inflate(layoutInflater)
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        setContentView(binding.root)

        // Map 초기화
        binding.osmMap.apply {
            setMultiTouchControls(true)
            controller.setZoom(19.0)
            controller.setCenter(GeoPoint(37.579881, 126.922745))
        }

        // 명지대 마커 추가
        val startPoint = GeoPoint(37.579881, 126.92237)
        val startMarker = Marker(binding.osmMap).apply {
            position = startPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = getDrawable(R.drawable.start_point)
            title = "Start point"
        }
        binding.osmMap.overlays.add(startMarker)





        /*
        // GeoPoint배열
        val points = mutableListOf(
            GeoPoint(37.554722, 126.970833), // 서울역
            GeoPoint(37.552349, 126.967478), // 중간 지점 1
            GeoPoint(37.550253, 126.972260), // 중간 지점 2
            GeoPoint(37.547001, 126.972912), // 중간 지점 3
            GeoPoint(37.529849, 126.964561)  // 용산역
        )
        */



        /*
        // 경로를 선으로 나타내기
        val polyline = Polyline().apply {
            for(i in points)
                addPoint(i)
            setColor(resources.getColor(R.color.purple_200))
            width = 20f // 선 두께 설정
        }
        map.overlayManager.add(polyline)
        */



        /*
        // 시작지점 마커
        val startMarker = Marker(binding.mapView).apply {
            position = points[0]
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Start point"
        }
        binding.mapView.overlays.add(startMarker)
        */



        /*
        // 종료 지점 마커
        val finishMarker = Marker(binding.mapView).apply {
            position = points[points.size-1]
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Finish point"
        }
        binding.mapView.overlays.add(finishMarker)
        */
    }
}