package com.example.mc_android

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mc_android.databinding.MapViewBinding
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class MapView: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = MapViewBinding.inflate(layoutInflater)
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        setContentView(binding.root)

        var startPoint: GeoPoint
        var finishPoint: GeoPoint


        // GeoPoint배열
//        val points = mutableListOf(
//            // GeoPoint(위도, 경도, 고도(기본값 0))
//            GeoPoint(37.554722, 126.970833), // 서울역
//            GeoPoint(37.552349, 126.967478), // 중간 지점 1
//            GeoPoint(37.550253, 126.972260), // 중간 지점 2
//            GeoPoint(37.547001, 126.972912), // 중간 지점 3
//            GeoPoint(37.529849, 126.964561)  // 용산역
//        ).also {
//            startPoint = it[0]
//            finishPoint = it[it.size-1]
//        }


        // 명지대 마커 추가
        startPoint = GeoPoint(37.579881, 126.922745)
        // finishPoint = points[points.size-1]
        Log.d("DEBUG", "$startPoint")

        // 시작 지점 마커
        val startMarker = Marker(binding.osmMap).apply {
            position = startPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = getDrawable(R.drawable.ic_start_point) // 아이콘 설정
            title = "Start point"
        }

        // 종료 지점 마커
//        val finishMarker = Marker(binding.mapView).apply {
//            position = finishPoint
//            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
//            icon = getDrawable(R.drawable.ic_end_point) // 아이콘 설정
//            title = "Finish point"
//        }

        // Map 초기화
        binding.osmMap.apply {
            setMultiTouchControls(true)
            controller.setZoom(19.0)
            controller.setCenter(GeoPoint(37.579881, 126.922745))
            overlays.add(startMarker) // start 마커 생성
//            overlays.add(finishMarker)
        }





        /*
        // GeoPoint배열 예제
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
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = getDrawable(R.drawable.ic_start_point) // 아이콘 설정
            title = "Start point"
        }
        */

        /*
        // 종료 지점 마커
        val finishMarker = Marker(binding.mapView).apply {
            position = points[points.size-1]
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = getDrawable(R.drawable.ic_start_point) // 아이콘 설정
            title = "Finish point"
        }
        */
    }
}