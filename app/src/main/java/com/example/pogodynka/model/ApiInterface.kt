package com.example.pogodynka.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/weather?")
    fun getCurrentWeather(@Query("q") q:String, @Query("units") units:String, @Query("appid") appid:String): Call<WeatherResponse>
}