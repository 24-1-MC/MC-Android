package com.example.mc_android.mydata

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class MyData(
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    val time: Int,
    val distance: Double,
    @ColumnInfo("elevation_gain") val elevationGain: Double,
    val weather: String,
    val temperature: Int,
    val humidity: Int,
    @ColumnInfo("location_file_name") val locationFileName: String,
)