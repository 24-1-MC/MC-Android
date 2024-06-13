package com.example.mc_android.mydata

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "my_data")
data class MyData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startAt: String,
    val endAt: String,
    val time: Int,
    val distance: Double,
    @ColumnInfo("total_elevation") val totalElevation: Double,
    @ColumnInfo("avg_face") val avgPace: Double,
    @ColumnInfo("weather_icon") val weatherIcon: String,
    val totalKcal: Int,
    val temperature: Int,
    val humidity: Int,
    @ColumnInfo("location_file_name") val locationFileName: String,
) : Parcelable