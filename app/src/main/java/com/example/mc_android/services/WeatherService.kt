package com.example.mc_android.services

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request

data class WeatherInfo(val icon: String, val temperature: Int, val humidity: Int)

// open weather map api키
private val key = "754918bb8d09858d91d5100163aa9c92"

fun getWeather(latitude: Double, longitude: Double): WeatherInfo? {
    val client = OkHttpClient()
    val url = "https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=$key&units=metric"

    val request = Request.Builder()
        .url(url)
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) return null

        val responseBody = response.body?.string() ?: return null
        val json = Gson().fromJson(responseBody, JsonObject::class.java)

        val icon = json.getAsJsonArray("weather")
            ?.get(0)?.asJsonObject?.get("icon")?.asString ?: return null
        val temperature = json.getAsJsonObject("main")
            ?.get("temp")?.asDouble?.toInt() ?: return null
        val humidity = json.getAsJsonObject("main")
            ?.get("humidity")?.asInt ?: return null

        return WeatherInfo(icon, temperature, humidity)
    }
}