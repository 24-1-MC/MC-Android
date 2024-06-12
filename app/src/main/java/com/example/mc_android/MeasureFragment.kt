package com.example.mc_android

import android.Manifest
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
import com.example.mc_android.mydata.MyData
import com.example.mc_android.mydata.MyDataDaoDatabase
import com.example.mc_android.services.GpxWriter
import com.example.mc_android.services.WeatherInfo
import com.example.mc_android.services.getCalories
import com.example.mc_android.services.getWeather
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MeasureFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var initLocation: Location? = null
    private var previousLocation: Location? = null
    private var previousAltitude: Double? = null
    private var weather: WeatherInfo? = null
    private var totalDistance = 0f
    private var totalElevation = 0.0
    private var isRunning = false
    private var isRecording = false
    private var time = 0L
    private var tick = 0L
    private var locationTick = 0
    private var gpx: GpxWriter? = null
    private val locationRequest = LocationRequest.create().apply {
        interval = 10000 // 측정 단위 = 10sec
        // fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private lateinit var startTimeIsoTimestamp: String
    private val isoUtcTimestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .withZone(ZoneId.of("UTC"))

        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMeasureBinding.inflate(layoutInflater)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        var averagePace = 0.0f

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                // 위치가 갱신될 경우 count
                if(previousLocation != locationResult.locations) {
                    locationTick++
                }

                for (location in locationResult.locations) {
                    // Debug
                    Log.d("DEBUG", "lat=${location.latitude} lon=${location.longitude} alt=${location.altitude}")

                    // 첫 위치, 날씨 기록
                    if(initLocation == null) initLocation = location
                    if(weather == null)
                        // 백그라운드 스레드풀
                        CoroutineScope(Dispatchers.Default).launch {
                            weather = getWeather(requireContext(), location.latitude, location.longitude)
                        }

                    // 누적 거리 계산
                    if (previousLocation != null) {
                        val distance = previousLocation!!.distanceTo(location)
                        totalDistance += distance
                    }

                    // 누적 상승 고도 계산
                    if (previousAltitude != null) {
                        val altitude = location.altitude-previousAltitude!!
                        if(altitude > 0)
                            totalElevation += altitude
                    }

                    // gpx 기록 갱신
                    gpx!!.append(location.latitude, location.longitude, location.altitude)

                    previousLocation = location
                    previousAltitude = location.altitude
                }
                tick = SystemClock.elapsedRealtime() - binding.timer.base

                // tick이 다 차면 평균 페이스 출력
                if(locationTick == 8) {
                    if(totalDistance != 0.0f)
                        averagePace = tick / totalDistance
                    if(averagePace < 2400)
                        binding.paceView.text = String.format("%d'%02d''", (averagePace/60).toInt(), (averagePace%60).toInt())
                    Log.d("DEBUG", "avg = $averagePace")
                    locationTick = 0
                }

                // 매번 갱신마다 누적 거리 출력
                binding.distanceView.text = String.format("%.2f", totalDistance / 1000)
            }
        }

        binding.action.setOnClickListener {
            // 위치서비스 권한이 없는 경우 다시 권한 요청
            if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Location service permission denied", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                return@setOnClickListener
            }

            if(!isRunning) {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
                if(!isRecording) {
                    val startTime = Instant.now()
                    // 현재 시간.gpx 파일 기록 시작
                    startTimeIsoTimestamp = isoUtcTimestampFormatter.format(startTime)
                    gpx = GpxWriter(requireContext()).apply { initialize() }
                    binding.timer.base = SystemClock.elapsedRealtime()
                } else {
                    binding.timer.base += (SystemClock.elapsedRealtime() - time)
                }
                binding.timer.start()
                isRunning = true
                isRecording = true
                binding.action.text = "멈춤"
                binding.stop.visibility = View.INVISIBLE
                Log.d("DEBUG", "started")
            } else {
                fusedLocationClient.removeLocationUpdates(locationCallback)
                binding.timer.stop()
                time = SystemClock.elapsedRealtime()
                tick = SystemClock.elapsedRealtime() - binding.timer.base
                isRunning = false
                binding.action.text = "재시작"
                binding.stop.visibility = View.VISIBLE
                Log.d("DEBUG", "paused")
            }
        }

        binding.stop.setOnClickListener {
            if(isRecording && !isRunning) {

                if(totalDistance < 10) {
                    binding.timer.base = SystemClock.elapsedRealtime()
                    binding.action.text = "Start"
                    binding.paceView.text = "0'00''"
                    binding.distanceView.text = "0.00"
                    binding.stop.visibility = View.INVISIBLE
                    previousLocation = null
                    totalDistance = 0f
                    isRecording = false
                    averagePace = 0.0f
                    Toast.makeText(context, "저장되지 않았습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }


                val endTime = Instant.now()

                AlertDialog.Builder(requireContext())
                    .setTitle("기록 완료")
                    .setPositiveButton("저장") { dialog, which ->
                        // gpx 기록 종료
                        gpx!!.finalizeGpx()

                        // 저장 코드를 구현해야함
                        // 시간값: Int
                        Log.d("DEBUG", "시간(sec) = ${(tick/1000).toInt()}")

                        // 거리: Double
                        Log.d("DEBUG", "거리(km) = ${(totalDistance/1000).toDouble()}")

                        // 상승 고도: Double
                        Log.d("DEBUG", "상승 고도(m) = $totalElevation")

                        // 평균 페이스: Double
                        var pace: Float
                        if(totalDistance == 0f) pace = 0f // infinity 예외처리
                        else pace = tick/totalDistance
                        if(averagePace > 2400) pace = 0f // pace가 너무 길면 0으로 표시
                        Log.d("DEBUG", "평균 페이스(sec) = ${pace.toDouble()}")

                        // 소모칼로리: Int
                        val cal = getCalories(totalDistance/1000, tick/3600000.0)
                        Log.d("DEBUG", "소모 칼로리 = $cal")

                        // 측정 위치: String
                        Log.d("DEBUG", "측정 위치 = ")

                        // 날씨: String


//                        Log.d("DEBUG", "날씨 = ${weather!!.icon}")

                        // 온도: Int
//                        Log.d("DEBUG", "온도 = ${weather!!.temperature}")

                        // 습도: Int
//                        Log.d("DEBUG", "습도 = ${weather!!.humidity}")

                        // gpx 파일 이름
                        Log.d("DEBUG", "파일명 = ${gpx!!.getFileName()}")

                        Log.d("DEBUG", "saved")
                        
                        binding.timer.base = SystemClock.elapsedRealtime()
                        binding.action.text = "Start"
                        binding.paceView.text = "0'00''"
                        binding.distanceView.text = "0.00"
                        binding.stop.visibility = View.INVISIBLE
                        previousLocation = null
                        totalDistance = 0f
                        isRecording = false
                        averagePace = 0.0f
                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()

                        Log.d("DEBUG", "날씨 = ${weather?.icon}")

                        // 온도: Int
                        Log.d("DEBUG", "온도 = ${weather?.temperature}")

                        // 습도: Int
                        Log.d("DEBUG", "습도 = ${weather?.humidity}")

                        // gpx 파일 이름
                        Log.d("DEBUG", "파일명 = ${gpx?.getFileName()}")

                        val db = MyDataDaoDatabase.getDatabase(requireContext())
                        val endTimeIsoUtcTimeStamp = isoUtcTimestampFormatter.format(endTime)

                        CoroutineScope(Dispatchers.IO).launch {
                            db!!.myDataDao().insert(MyData(
                                startAt = startTimeIsoTimestamp,
                                endAt = endTimeIsoUtcTimeStamp,
                                time = (tick/1000).toInt(),
                                distance = (totalDistance/1000).toDouble(),
                                totalElevation = totalElevation,
                                avgFace = pace.toDouble(),
                                weatherIcon = weather!!.icon,
                                temperature = weather!!.temperature,
                                humidity = weather!!.humidity,
                                locationFileName = gpx!!.getFileName()
                            ))

                            withContext(Dispatchers.Main) {
                                Log.d("DEBUG", "saved")
                                binding.timer.base = SystemClock.elapsedRealtime()
                                binding.action.text = "Start"
                                binding.paceView.text = "0'00''"
                                binding.distanceView.text = "0.00"
                                binding.stop.visibility = View.INVISIBLE
                                previousLocation = null
                                totalDistance = 0f
                                isRecording = false
                                averagePace = 0.0f
                                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                    .setNegativeButton("삭제") { dialog, which ->
                        binding.timer.base = SystemClock.elapsedRealtime()
                        binding.action.text = "시작"
                        binding.paceView.text = "0'00''"
                        binding.distanceView.text = "0.00"
                        binding.stop.visibility = View.INVISIBLE
                        previousLocation = null
                        totalDistance = 0f
                        isRecording = false
                        averagePace = 0.0f
                        Toast.makeText(context, "삭제 완료", Toast.LENGTH_SHORT).show()
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
}
