package com.example.mc_android.services

import android.content.Context
import android.content.Intent
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.ZoneId

class GpxWriter(context: Context, startTimeIsoTimestamp: String) {
    // 현재 타임스탬프를 파일 이름으로 사용
    private val fileName = "${startTimeIsoTimestamp}.gpx"
    private val file = File(context.filesDir, fileName).apply { createNewFile() }
    private val writer = BufferedWriter(FileWriter(file))

    fun initialize() {
        writer.apply {
            append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            append("<gpx version=\"1.1\" creator=\"made_by_jpark/한결/지수/SJ\">\n")
            append("<trk>\n")
            append("  <name>OutdoorRunning</name>\n")
            append("  <trkseg>\n")
        }
    }

    fun append(latitude: Double, longitude: Double, altitude: Double) {
        val currentTime = Instant.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .withZone(ZoneId.of("UTC"))
        val formattedTime = formatter.format(currentTime)
        writer.apply {
            append("    <trkpt lat=\"$latitude\" lon=\"$longitude\">\n")
            append("      <ele>$altitude</ele>\n")
            append("      <time>${formattedTime}</time>\n")
            append("    </trkpt>\n")
        }
    }

    fun finalizeGpx() {
        writer.apply {
            append("  </trkseg>\n")
            append("</trk>\n")
            append("</gpx>\n")
            close()
        }
    }

    fun getFileName(): String {
        return fileName
    }
}







