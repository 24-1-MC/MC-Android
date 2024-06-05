package com.example.mc_android.services

import android.content.Context
import org.osmdroid.util.GeoPoint
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

// gpx 파일을 BufferedRedaer를 통해 입력 - GeoPoint객체를 MutableList에 저장하여 리턴
class GpxReader(context: Context, private val fileName: String) {
    private val file = File(context.filesDir, fileName).apply { createNewFile() }
    private val output = mutableListOf<GeoPoint>()

    fun parse(): MutableList<GeoPoint> {
        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = documentBuilder.parse(file)
        document.documentElement.normalize()

        val trkpts = document.getElementsByTagName("trkpt")

        for (i in 0 until trkpts.length) {
            val trkpt = trkpts.item(i) as Element
            val lat = trkpt.getAttribute("lat").toDouble()
            val lon = trkpt.getAttribute("lon").toDouble()
            val ele = trkpt.getElementsByTagName("ele").item(0).textContent.toDouble()
            output.add(GeoPoint(lat, lon, ele))
        }

        return output
    }
}