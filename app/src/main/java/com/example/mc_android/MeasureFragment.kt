package com.example.mc_android

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mc_android.databinding.FragmentMeasureBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlin.math.round

class MeasureFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var previousLocation: Location? = null
    private var totalDistance: Float = 0f
    private var isRunning = false
    private var isRecording = false
    private var time = 0L
    private val locationRequest = LocationRequest.create().apply {
        interval = 10000 // 측정 단위 = 10sec
        // fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMeasureBinding.inflate(layoutInflater)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        var tick: Long
        var averagePace = 0.0f

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (previousLocation != null) {
                        val distance = previousLocation!!.distanceTo(location)
                        totalDistance += distance
                        Log.d("DEBUG", "location updated")
                    }
                    previousLocation = location
                }
                tick = SystemClock.elapsedRealtime() - binding.timer.base

                // 평균 페이스 출력
                if(totalDistance != 0.0f)
                    averagePace = tick / totalDistance
                binding.paceView.text = String.format("%d'%02d''", (averagePace/60).toInt(), (averagePace%60).toInt())

                // 누적 거리 출력
                binding.distanceView.text = String.format("%.2f", totalDistance / 1000)

                Log.d("DEBUG", "${locationResult.locations}")
            }
        }

        binding.action.setOnClickListener {
            // 위치서비스 권한이 없는 경우
            if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Location service permission denied", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                return@setOnClickListener
            }

            if(!isRunning) {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
                if(!isRecording) {
                    binding.timer.base = SystemClock.elapsedRealtime()
                } else {
                    binding.timer.base += (SystemClock.elapsedRealtime() - time)
                }
                binding.timer.start()
                isRunning = true
                isRecording = true
                binding.action.text = "Pause"
                binding.stop.visibility = View.INVISIBLE
                Log.d("DEBUG", "started")
            } else {
                fusedLocationClient.removeLocationUpdates(locationCallback)
                binding.timer.stop()
                time = SystemClock.elapsedRealtime()
                isRunning = false
                binding.action.text = "Resume"
                binding.stop.visibility = View.VISIBLE
                Log.d("DEBUG", "paused")
            }
        }

        binding.stop.setOnClickListener {
            if(isRecording && !isRunning) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Complete")
                    .setPositiveButton("Save") { dialog, which ->

                        // 저장 코드를 구현해야함

                        Log.d("DEBUG", "saved")
                    }
                    .setNegativeButton("Discard") { dialog, which ->
                        binding.timer.base = SystemClock.elapsedRealtime()
                        binding.action.text = "Start"
                        binding.paceView.text = "0'00''"
                        binding.distanceView.text = "0.00"
                        binding.stop.visibility = View.INVISIBLE
                        previousLocation = null
                        totalDistance = 0f
                        isRecording = false
                        averagePace = 0.0f
                        Log.d("DEBUG", "deleted")
                    }
                    .create()
                    .show()
            }
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }



/*
    칼로리 계산식
    private fun calculateCaloriesBurned(gpxData: GPXData, userData: UserData): Double {
        val durationHours = gpxData.duration / (1000 * 60 * 60).toDouble()
        val speed = gpxData.distance / (gpxData.duration / 1000.0) * 3.6 // km/h

        // Simplified METS values based on speed
        val mets = when {
            speed < 4 -> 2.0
            speed < 6 -> 3.0
            speed < 8 -> 6.0
            else -> 8.0
        }

        // Calories burned formula
        return mets * userData.weight * durationHours
    }
    */
}
