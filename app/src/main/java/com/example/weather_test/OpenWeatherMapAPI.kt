package com.example.weather_test

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapAPI {
    @GET("weather")
    fun geoCoordinate(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
    ): Call<OpenWeatherMapResponse>
}