package com.example.pogodynka.model

data class Forecast(
    //Name of day for forecast
    var dayName: String = "",
    //name of weather for icon
    var weatherName: String = "",
    // temp: min-max celsius
    var temp: String = "",
) {
}