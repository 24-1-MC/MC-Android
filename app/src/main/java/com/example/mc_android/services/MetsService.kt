package com.example.mc_android.services

//칼로리 계산식
fun getCalories(distance: Float, duration: Double): Double {
    val speed = distance / duration // km/h

    // 평균 속도를 통한 METS값 산출
    val mets = when {
        speed < 4 -> 2.0
        speed < 6 -> 3.0
        speed < 8 -> 6.0
        else -> 8.0
    }

    // 칼로리 계산식
    return mets * /*체중 **/ duration
}