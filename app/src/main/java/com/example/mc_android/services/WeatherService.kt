package com.example.mc_android.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request

data class WeatherInfo(val icon: String, val temperature: Int, val humidity: Int)

// open weather map api키
private val key = "754918bb8d09858d91d5100163aa9c92"

fun getWeather(context: Context, latitude: Double, longitude: Double): WeatherInfo? {
    if (!isNetworkAvailable(context)) {
        // 네트워크에 연결되어 있지 않은 경우 null 반환
        return WeatherInfo("99", 99, 99)
    }

    val client = OkHttpClient()
    // query
    val url = "https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=$key&units=metric"

    val request = Request.Builder()
        .url(url)
        .build()

    // query 전송
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

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}