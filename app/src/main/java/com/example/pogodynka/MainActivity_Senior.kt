package com.example.pogodynka

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.pogodynka.ViewModel.LocationVM
import com.example.pogodynka.ViewModel.LocationVMFactory
import com.example.pogodynka.ViewModel.adapters.ForecastAdapter
import com.example.pogodynka.model.Forecast
import com.example.pogodynka.model.ForecastResponse
import com.example.pogodynka.model.WeatherResponse
import com.example.pogodynka.model.WeatherService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MainActivity_Senior : AppCompatActivity() {
    private lateinit var locVM: LocationVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_senior)
        val factoryLocVM = LocationVMFactory((requireNotNull(this).application), this.applicationContext)
        locVM = ViewModelProvider(this, factoryLocVM).get(LocationVM::class.java)
        locVM.cityName.setValue(intent.getStringExtra("cityName"))

        val retrofit = Retrofit.Builder()
            .baseUrl(MainActivity.BaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(WeatherService::class.java)
        val call = service.getCurrentWeather(locVM.cityName.value!!, "metric",
            MainActivity.AppId
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
                            locVM.weatherDesc = "Thunderstorm"
                        }
                        "Drizzle" -> {
                            findViewById<TextView>(R.id.weatherName).text = "LEKKI DESZCZ"
                            findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.showers)
                            locVM.weatherDesc = "Drizzle"
                        }
                        "Rain" -> {
                            findViewById<TextView>(R.id.weatherName).text = "OPADY DESZCZU"
                            findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.showers)
                            locVM.weatherDesc = "Rain"
                        }
                        "Snow" -> {
                            findViewById<TextView>(R.id.weatherName).text = "OPADY ŚNIEGU"
                            findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.snow)
                            locVM.weatherDesc = "Snow"
                        }
                        "Mist" -> {
                            findViewById<TextView>(R.id.weatherName).text = "MGŁA"
                            findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.foggy)
                            locVM.weatherDesc = "Mist"
                        }
                        "Clear" -> {
                            findViewById<TextView>(R.id.weatherName).text = "SŁONECZNIE"
                            findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.sunny)
                            locVM.weatherDesc = "Clear"
                        }
                        "Clouds" -> {
                            findViewById<TextView>(R.id.weatherName).text = "ZACHMURZENIE"
                            findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.mostly_cloudy)
                            locVM.weatherDesc = "Clouds"
                        }
                    }


                    //temp. press, humidity
                    findViewById<TextView>(R.id.temp).text = "${weatherResponse!!.main!!.temp!!.roundToInt().toString()} °C"
                    findViewById<TextView>(R.id.pressure).text = "${weatherResponse!!.main!!.pressure!!.roundToInt().toInt().toString()} hPa"
                    findViewById<TextView>(R.id.humidity).text = "${weatherResponse!!.main!!.humidity!!.roundToInt().toInt().toString()} %"
                    locVM.temp = "${weatherResponse!!.main!!.temp!!.roundToInt().toString()} °C"
                    locVM.pres = "${weatherResponse!!.main!!.pressure!!.roundToInt().toInt().toString()} hPa"
                    locVM.hum = "${weatherResponse!!.main!!.humidity!!.roundToInt().toInt().toString()} %"

                    //sunrise, sunset, date

                    findViewById<TextView>(R.id.sunriseTime).text = epochToTime(weatherResponse!!.sys!!.sunrise)
                    findViewById<TextView>(R.id.sunsetTime).text = epochToTime(weatherResponse!!.sys!!.sunset)
                    locVM.sunrise = epochToTime(weatherResponse!!.sys!!.sunrise)
                    locVM.sunset = epochToTime(weatherResponse!!.sys!!.sunset)

                    val calendar = Calendar.getInstance()

                    var dateInfo = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.time)

                    println(dateInfo.split(",")[0])

                    when(dateInfo.split(",")[0]){
                        "poniedzałek" -> {
                            findViewById<TextView>(R.id.dayName).text = "Poniedziałek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                            locVM.date = "Poniedziałek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                        }
                        "wtorek" -> {
                            findViewById<TextView>(R.id.dayName).text = "Wtorek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                            locVM.date = "Wtorek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                        }
                        "środa" -> {
                            findViewById<TextView>(R.id.dayName).text = "Środa, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                            locVM.date = "Środa, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                        }
                        "czwartek" -> {
                            findViewById<TextView>(R.id.dayName).text = "Czwartek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                            locVM.date = "Czwartek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                        }
                        "piątek" -> {
                            findViewById<TextView>(R.id.dayName).text = "Piątek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                            locVM.date = "Piątek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                        }
                        "sobota" -> {
                            findViewById<TextView>(R.id.dayName).text = "Sobota, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                            locVM.date = "Sobota, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                        }
                        "niedziela" -> {
                            findViewById<TextView>(R.id.dayName).text = "Niedziela, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                            locVM.date = "Niedziela, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                        }
                    }

                }

            }
        })

        //Update loc info
        findViewById<TextView>(R.id.cityName).text = intent.getStringExtra("cityName")

        val swipe : SwipeRefreshLayout = findViewById(R.id.refreshLayout)

        swipe.setOnRefreshListener {
            //refresh api data
            val retrofit = Retrofit.Builder()
                .baseUrl(MainActivity.BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(WeatherService::class.java)
            val call = service.getCurrentWeather(locVM.cityName.value!!, "metric",
                MainActivity.AppId
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
                        findViewById<TextView>(R.id.temp).text = "${weatherResponse!!.main!!.temp.roundToInt().toString()} °C"
                        findViewById<TextView>(R.id.pressure).text = "${weatherResponse!!.main!!.pressure.roundToInt().toString()} hPa"
                        findViewById<TextView>(R.id.humidity).text = "${weatherResponse!!.main!!.humidity.roundToInt().toString()} %"


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
            swipe.isRefreshing = false
        }
        findViewById<Button>(R.id.seniorSwitch).setOnClickListener {
            val intent = Intent(this@MainActivity_Senior, MainActivity::class.java)
            //Load API data
            //getCurrentWeather(locVM.cityName.value!!)
            intent.putExtra("cityName", locVM.cityName.value)
            startActivity(intent)
            finish()
        }
    }
    private fun epochToTime(time: Long): String {
        val format = "HH:mm:ss" // you can add the format you need
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
}