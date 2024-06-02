package com.example.mc_android

import android.os.Bundle
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

        // Initialize the MapView
        binding.osmMap.apply {
            setMultiTouchControls(true)
            controller.setZoom(19.0)
            controller.setCenter(GeoPoint(37.579881, 126.922745)) // Example: Center on San Francisco
        }

        // Add a marker
        val startPoint = GeoPoint(37.579881, 126.922745)
        val startMarker = Marker(binding.osmMap).apply {
            position = startPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Start point"
        }
        binding.osmMap.overlays.add(startMarker)



    }
}