package com.example.mc_android.mydata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MyData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startAt: String,
    val endAt: String,
    val time: Int,
    val distance: Double,
    @ColumnInfo("total_elevation") val totalElevation: Double,
    @ColumnInfo("avg_face") val avgFace: Double,
    @ColumnInfo("weather_icon") val weatherIcon: String,
    val temperature: Int,
    val humidity: Int,
    @ColumnInfo("location_file_name") val locationFileName: String,
)