package com.example.mc_android

import java.io.BufferedWriter
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GpxWriter {
    // 현재 시간 값을 파일 이름으로 사용
    private var currentTime = LocalDateTime.now()
    private val fileName = "${formatFileName(currentTime)}.gpx"
    private val file = File(fileName).apply { createNewFile() }
    private val writer = BufferedWriter(file.writer())

    fun initialize() {
        writer.apply {
            append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            append("<gpx version=\"1.1\" creator=\"MJU-MC-2024\">\n")
            append("<trk><name>OutdoorRunning</name><trkseg>\n")
        }
    }

    fun append(latitude: Double, longitude: Double, altitude: Double) {
        currentTime = LocalDateTime.now()
        writer.apply {
            append("<trkpt lat=\"$latitude\" lon=\"$longitude\">\n")
            append("<ele>$altitude</ele>\n")
            append("<time>${formatTime(currentTime)}</time>\n")
            append("</trkpt>\n")
        }
    }

    fun finalizeGpx() {
        writer.apply {
            append("</trkseg></trk>\n")
            append("</gpx>\n")
            close()
        }
    }



    private fun formatTime(currentTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        return currentTime.format(formatter)
    }
    private fun formatFileName(currentTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        return currentTime.format(formatter)
    }
}







