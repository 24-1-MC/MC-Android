package com.example.mc_android.services

import android.content.Context
import org.osmdroid.util.GeoPoint
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

// gpx 파일을 BufferedRedaer를 통해 입력
// GeoPoint객체를 MutableList에 저장하여 리턴
class GpxReader(context: Context, fileName: String) {
    private val fileName = fileName
    private val file = File(context.filesDir, fileName).apply { createNewFile() }
    private val reader = BufferedReader(FileReader(file))





//    	public GeoPoint(final double aLatitude, final double aLongitude, final double aAltitude) {
//		this.mLatitude = aLatitude;
//		this.mLongitude = aLongitude;
//		this.mAltitude = aAltitude;
//	}
}