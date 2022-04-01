package com.example.pogodynka

import android.location.*
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.pogodynka.ViewModel.LocationVM
import com.example.pogodynka.ViewModel.LocationVMFactory
import com.example.pogodynka.model.WeatherResponse
import com.example.pogodynka.model.WeatherService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var locVM: LocationVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val factoryCardVM = LocationVMFactory((requireNotNull(this).application), this.applicationContext)
        locVM = ViewModelProvider(this, factoryCardVM).get(LocationVM::class.java)
        locVM.cityName.setValue(intent.getStringExtra("cityName"))

        val retrofit = Retrofit.Builder()
            .baseUrl(SplashScreen.BaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(WeatherService::class.java)
        val call = service.getCurrentWeather(locVM.cityName.value!!, "metric",
            SplashScreen.AppId
        )

        call.enqueue(object: Callback<WeatherResponse> {
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Nie udalo sie pobrac danych", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.code() === 200) {
                    val weatherResponse = response.body()!!

                    //description + icon
                    when(weatherResponse!!.weather[0]!!.main){
                        "Thunderstorm" -> {
                            findViewById<TextView>(R.id.weatherName).text = "BURZA"
                            findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.thunderstroms)
                        }
                        "Drizzle" -> {
                            findViewById<TextView>(R.id.weatherName).text = "LEKKI DESZCZ"
                            findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.showers)
                        }
                        "Rain" -> {
                            findViewById<TextView>(R.id.weatherName).text = "OPADY DESZCZU"
                            findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.showers)
                        }
                        "Snow" -> {
                            findViewById<TextView>(R.id.weatherName).text = "OPADY ŚNIEGU"
                            findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.snow)
                        }
                        "Mist" -> {
                            findViewById<TextView>(R.id.weatherName).text = "MGŁA"
                            findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.foggy)
                        }
                        "Clear" -> {
                            findViewById<TextView>(R.id.weatherName).text = "SŁONECZNIE"
                            findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.sunny)
                        }
                        "Clouds" -> {
                            findViewById<TextView>(R.id.weatherName).text = "ZACHMURZENIE"
                            findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.mostly_cloudy)
                        }
                    }


                    //temp. press, humidity
                    findViewById<TextView>(R.id.temp).text = "${weatherResponse!!.main!!.temp.toString()} °C"
                    findViewById<TextView>(R.id.pressure).text = "${weatherResponse!!.main!!.pressure.toInt().toString()} hPa"
                    findViewById<TextView>(R.id.humidity).text = "${weatherResponse!!.main!!.humidity.toInt().toString()} %"


                    //sunrise, sunset, date

                    findViewById<TextView>(R.id.sunriseTime).text = epochToTime(weatherResponse!!.sys!!.sunrise)
                    findViewById<TextView>(R.id.sunsetTime).text = epochToTime(weatherResponse!!.sys!!.sunset)

                    val calendar = Calendar.getInstance()

                    var dateInfo = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.time)

                    println(dateInfo.split(",")[0])

                    when(dateInfo.split(",")[0]){
                        "poniedzałek" -> {
                            findViewById<TextView>(R.id.dayName).text = "Poniedziałek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                        }
                        "wtorek" -> {
                            findViewById<TextView>(R.id.dayName).text = "Wtorek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                        }
                        "środa" -> {
                            findViewById<TextView>(R.id.dayName).text = "Środa, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                        }
                        "czwartek" -> {
                            findViewById<TextView>(R.id.dayName).text = "Czwartek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                        }
                        "piątek" -> {
                            findViewById<TextView>(R.id.dayName).text = "Piątek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                        }
                        "sobota" -> {
                            findViewById<TextView>(R.id.dayName).text = "Sobota, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                        }
                        "niedziela" -> {
                            findViewById<TextView>(R.id.dayName).text = "Niedziela, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                        }
                    }

                }

            }
        })

        //Update loc info
        findViewById<TextView>(R.id.cityName).text = intent.getStringExtra("cityName")


    }

    fun getWeekDayName(s: String?): String? {
        val dtfInput: DateTimeFormatter = DateTimeFormatter.ofPattern("u-M-d", Locale.ENGLISH)
        val dtfOutput: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH)
        return LocalDate.parse(s, dtfInput).format(dtfOutput)
    }

    private fun epochToTime(time: Long): String {
        val format = "HH:mm" // you can add the format you need
        val sdf = SimpleDateFormat(format, Locale.getDefault()) // default local
        sdf.timeZone = TimeZone.getDefault() // set anytime zone you need
        return sdf.format(Date(time * 1000))
    }
    private fun epochToDate(time: Long): String {
        val format = "dd.MM" // you can add the format you need
        val sdf = SimpleDateFormat(format, Locale.getDefault()) // default local
        sdf.timeZone = TimeZone.getDefault() // set anytime zone you need
        return sdf.format(Date(time * 1000))
    }

    private fun epochToWeekDay(time: Long): String {
        val format = "yyyy-dd-MM" // you can add the format you need
        val sdf = SimpleDateFormat(format, Locale.getDefault()) // default local
        sdf.timeZone = TimeZone.getDefault() // set anytime zone you need
        return sdf.format(Date(time * 1000))
    }

    companion object{
        var BaseUrl = "https://api.openweathermap.org/"
        var AppId = "247efa34607080a70fe2d53cce68d206"
    }
}