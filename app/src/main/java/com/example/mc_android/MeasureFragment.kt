package com.example.mc_android

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.SystemClock
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
    // main activity에서 접근하기 위해 public으로 변경
    var isRecording = false
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
        val sharedPreferences = requireContext().getSharedPreferences("PREF", Context.MODE_PRIVATE)

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
                Toast.makeText(context, "위치서비스 권한이 없습니다", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                return@setOnClickListener
            }

            // 체중값이 없는 경우
            if(sharedPreferences.getFloat("WEIGHT", -1.0f) == -1.0f) {
                Toast.makeText(context, "체중이 입력되지 않았습니다", Toast.LENGTH_SHORT).show()
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("체중을 입력하세요")

                val input = EditText(requireContext())
                input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                builder.setView(input)

                builder.setPositiveButton("저장") { dialog, which ->
                    try {
                        val weight = input.text.toString().toDouble()
                        sharedPreferences.edit().putFloat("WEIGHT", weight.toFloat()).apply()
                        Toast.makeText(context, "저장되었습니다", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "잘못된 입력입니다", Toast.LENGTH_SHORT).show()
                    }
                }
                builder.setNegativeButton("취소") { dialog, which -> dialog.cancel() }

                builder.show()
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
            // 기록이 너무 짧으면 저장하지 않음
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

                        // 평균 페이스: Double
                        var pace: Float
                        if(totalDistance == 0f) pace = 0f // infinity 예외처리
                        else pace = tick/totalDistance
                        if(averagePace > 2400) pace = 0f // pace가 너무 길면 0으로 표시
                        Log.d("DEBUG", "평균 페이스(sec) = ${pace.toDouble()}")

                        // 소모칼로리: Int
                        val weight = sharedPreferences.getFloat("WEIGHT", -1.0f).toDouble()
                        val cal = getCalories(totalDistance/1000, weight, tick/3600000.0)

                        val db = MyDataDaoDatabase.getDatabase(requireContext())
                        val endTimeIsoUtcTimeStamp = isoUtcTimestampFormatter.format(endTime)

                        CoroutineScope(Dispatchers.IO).launch {
                            db!!.myDataDao().insert(MyData(
                                startAt = startTimeIsoTimestamp,
                                endAt = endTimeIsoUtcTimeStamp,
                                time = (tick/1000).toInt(),
                                distance = totalDistance.toDouble(),
                                totalElevation = totalElevation,
                                avgPace = pace.toDouble(),
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
