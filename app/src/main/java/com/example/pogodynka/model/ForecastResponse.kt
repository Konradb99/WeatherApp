package com.example.pogodynka.model

import com.google.gson.annotations.SerializedName

class ForecastResponse {
    @SerializedName("cod")
    var cod: String? = null
    @SerializedName("message")
    var message: Float = 0.toFloat()
    @SerializedName("cnt")
    var cnt: Float = 0.toFloat()
    @SerializedName("list")
    var list = ArrayList<List>()
    @SerializedName("city")
    var city: CityFore? = null
}

class List{
    @SerializedName("dt")
    var dt: Long = 0
    @SerializedName("main")
    var main: MainFore? = null
    @SerializedName("weather")
    var weather = ArrayList<WeatherFore>()
    @SerializedName("clouds")
    var clouds: CloudsFore? = null
    @SerializedName("wind")
    var wind: WindFore? = null
    @SerializedName("visibility")
    var visibility: Float  = 0.toFloat()
    @SerializedName("pop")
    var pop: Float  = 0.toFloat()
    @SerializedName("rain")
    var rain: RainFore? = null
    @SerializedName("sys")
    var sys: SysFore? = null
    @SerializedName("dt_txt")
    var dt_txt: String? = null
}

class CityFore{
    @SerializedName("id")
    var id: Int = 0
    @SerializedName("name")
    var name: String? = null
    @SerializedName("coord")
    var coord: CoordFore? = null
    @SerializedName("country")
    var country: String? = null
    @SerializedName("population")
    var population: Long = 0
    @SerializedName("timezone")
    var timezone: Long = 0
    @SerializedName("sunrise")
    var sunrise: Long = 0
    @SerializedName("sunset")
    var sunset: Long = 0
}

class CoordFore{
    @SerializedName("lat")
    var lat: Float = 0.toFloat()
    @SerializedName("lon")
    var lon: Float = 0.toFloat()
}

class WeatherFore{
    @SerializedName("id")
    var id: Int = 0
    @SerializedName("main")
    var main: String? = null
    @SerializedName("description")
    var description: String? = null
    @SerializedName("icon")
    var icon: String? = null
}

class MainFore{
    @SerializedName("temp")
    var temp: Float? = 0.toFloat()
    @SerializedName("feels_like")
    var feels_like: Float? = 0.toFloat()
    @SerializedName("temp_min")
    var temp_min: Float? = 0.toFloat()
    @SerializedName("temp_max")
    var temp_max: Float? = 0.toFloat()
    @SerializedName("pressure")
    var pressure: Float? = 0.toFloat()
    @SerializedName("sea_level")
    var sea_level: Float? = 0.toFloat()
    @SerializedName("grnd_level")
    var grnd_level: Float? = 0.toFloat()
    @SerializedName("humidity")
    var humidity: Float? = 0.toFloat()
    @SerializedName("temp_kf")
    var temp_kf: Float? = 0.toFloat()
}

class CloudsFore{
    @SerializedName("all")
    var all: Float = 0.toFloat()
}

class WindFore {
    @SerializedName("speed")
    var speed: Float = 0.toFloat()
    @SerializedName("deg")
    var deg: Float = 0.toFloat()
    @SerializedName("gust")
    var gust: Float = 0.toFloat()
}

class RainFore {
    @SerializedName("3h")
    var h3: Float = 0.toFloat()
}

class SysFore {
    @SerializedName("pod")
    var pod: String? = null
}