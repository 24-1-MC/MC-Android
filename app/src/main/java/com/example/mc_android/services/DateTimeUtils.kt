package com.example.mc_android.services

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeUtils {
    fun convertToLocalDate(iso8601Timestamp: String): String {
        // ISO-8601 형식의 타임스탬프를 ZonedDateTime 객체로 변환
        val zonedDateTime = ZonedDateTime.parse(iso8601Timestamp, DateTimeFormatter.ISO_DATE_TIME)

        // 기기의 기본 시간대로 ZonedDateTime을 변환
        val localDateTime = zonedDateTime.withZoneSameInstant(java.time.ZoneId.systemDefault())

        // 변환된 시간을 원하는 형식으로 포맷팅
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        return localDateTime.format(formatter)
    }
}