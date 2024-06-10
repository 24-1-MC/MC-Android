package com.example.mc_android

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mc_android.databinding.MapViewBinding
import com.example.mc_android.services.GpxReader
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class MapView: AppCompatActivity() {
    private var gpxFile: GpxReader? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = MapViewBinding.inflate(layoutInflater)
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        setContentView(binding.root)
//        인텐트 작성
//        intent.getIntExtra("fileName", 0).toString().also {
//            if(it != "0") {
//                gpxFile = GpxReader(applicationContext, it)
//            }
//        }
        gpxFile = GpxReader(applicationContext, "20240605_124602.gpx")
        var startPoint: GeoPoint
        var finishPoint: GeoPoint
        var maxLatitude: Double
        var minLatitude: Double
        var maxLongtitude: Double
        var minLongtitude: Double
        var center: GeoPoint
        var zoomLevel: Double

        if(gpxFile == null) return

        // GeoPoint배열
        val points = gpxFile!!.parse().also {
            startPoint = it[0]
            finishPoint = it[it.size-1]
            maxLatitude = it[0].latitude
            minLatitude = it[0].latitude
            maxLongtitude = it[0].longitude
            minLongtitude = it[0].longitude
            for(p in it) {
                if(maxLatitude < p.latitude) maxLatitude = p.latitude
                if(minLatitude > p.latitude) minLatitude = p.latitude
                if(maxLongtitude < p.longitude) maxLongtitude = p.longitude
                if(minLongtitude > p.longitude) minLongtitude = p.longitude
            }

            // 맵 뷰 센터 좌표 설정
            center = GeoPoint((maxLatitude+minLatitude)/2, (maxLongtitude+minLongtitude)/2)
            Log.d("DEBUG", "${maxLongtitude - minLongtitude}")

            // 줌 레벨 설정
            val length = maxLongtitude - minLongtitude
            if(length < 0.0025) zoomLevel = 19.0
            else if(length < 0.005) zoomLevel = 18.0
            else if(length < 0.01) zoomLevel = 17.0
            else zoomLevel = 16.0
        }

        // 시작 지점 마커
        val startMarker = Marker(binding.osmMap).apply {
            position = startPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = getDrawable(R.drawable.ic_start_point) // 아이콘 설정
            title = "Start point"
        }

        // 종료 지점 마커
        val finishMarker = Marker(binding.osmMap).apply {
            position = finishPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = getDrawable(R.drawable.ic_end_point) // 아이콘 설정
            title = "Finish point"
        }

        // 경로를 선으로 나타내기
        val polyline = Polyline().apply { for(i in points) addPoint(i) }
        polyline.outlinePaint.apply {
            color = ContextCompat.getColor(applicationContext, R.color.material_purple)
            strokeWidth = 20f // 선 두께 설정
            strokeCap = Paint.Cap.ROUND // 모서리 둥글게
        }

        // Map 초기화
        binding.osmMap.apply {
            setMultiTouchControls(true)
            controller.setZoom(zoomLevel)
            controller.setCenter(center)
            overlays.add(startMarker) // start 마커 생성
            overlays.add(finishMarker)
            overlayManager.add(polyline)
        }
    }
}