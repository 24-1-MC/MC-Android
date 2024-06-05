package com.example.mc_android.services

import android.content.Context
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GpxWriter(context: Context) {
    // 현재 시간 값을 파일 이름으로 사용
    private var currentTime = LocalDateTime.now()
    private val fileName = "${formatFileName(currentTime)}.gpx"
    private val file = File(context.filesDir, fileName).apply { createNewFile() }
    private val writer = BufferedWriter(FileWriter(file))

    fun initialize() {
        writer.apply {
            append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            append("<gpx version=\"1.1\" creator=\"made_by_jpark/한결/지수/성준\">\n")
            append("<trk>\n")
            append("  <name>OutdoorRunning</name>\n")
            append("  <trkseg>\n")
        }
    }

    fun append(latitude: Double, longitude: Double, altitude: Double) {
        currentTime = LocalDateTime.now()
        writer.apply {
            append("    <trkpt lat=\"$latitude\" lon=\"$longitude\">\n")
            append("      <ele>$altitude</ele>\n")
            append("      <time>${formatTime(currentTime)}</time>\n")
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



    private fun formatTime(currentTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        return currentTime.format(formatter)
    }
    private fun formatFileName(currentTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        return currentTime.format(formatter)
    }
}







