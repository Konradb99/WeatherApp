package com.example.pogodynka

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
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


class MainActivity : AppCompatActivity() {
    lateinit var locVM: LocationVM
    private var fragmentNormSenior: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val factoryLocVM = LocationVMFactory((requireNotNull(this).application), this.applicationContext)
        locVM = ViewModelProvider(this, factoryLocVM).get(LocationVM::class.java)
        locVM.cityName.setValue(intent.getStringExtra("cityName"))

        val retrofit = Retrofit.Builder()
            .baseUrl(BaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(WeatherService::class.java)
        val call = service.getCurrentWeather(locVM.cityName.value!!, "metric",
            AppId
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
                            findViewById<TextView>(R.id.weatherName).text = "OPADY ??NIEGU"
                            findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.snow)
                            locVM.weatherDesc = "Snow"
                        }
                        "Mist" -> {
                            findViewById<TextView>(R.id.weatherName).text = "MG??A"
                            findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.foggy)
                            locVM.weatherDesc = "Mist"
                        }
                        "Clear" -> {
                            findViewById<TextView>(R.id.weatherName).text = "S??ONECZNIE"
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
                    findViewById<TextView>(R.id.temp).text = "${weatherResponse!!.main!!.temp!!.roundToInt().toString()} ??C"
                    findViewById<TextView>(R.id.pressure).text = "${weatherResponse!!.main!!.pressure!!.roundToInt().toInt().toString()} hPa"
                    findViewById<TextView>(R.id.humidity).text = "${weatherResponse!!.main!!.humidity!!.roundToInt().toInt().toString()} %"
                    locVM.temp = "${weatherResponse!!.main!!.temp!!.roundToInt().toString()} ??C"
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
                        "poniedza??ek" -> {
                            findViewById<TextView>(R.id.dayName).text = "Poniedzia??ek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                            locVM.date = "Poniedzia??ek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                        }
                        "wtorek" -> {
                            findViewById<TextView>(R.id.dayName).text = "Wtorek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                            locVM.date = "Wtorek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                        }
                        "??roda" -> {
                            findViewById<TextView>(R.id.dayName).text = "??roda, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                            locVM.date = "??roda, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                        }
                        "czwartek" -> {
                            findViewById<TextView>(R.id.dayName).text = "Czwartek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                            locVM.date = "Czwartek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                        }
                        "pi??tek" -> {
                            findViewById<TextView>(R.id.dayName).text = "Pi??tek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                            locVM.date = "Pi??tek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
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


        //Forecast for next days
        val dataLive = MutableLiveData<List<Forecast>>()

        var layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        var forecastAdapter = ForecastAdapter(dataLive, this.applicationContext)
        dataLive.observe(this, {forecastAdapter.notifyDataSetChanged()})

        findViewById<RecyclerView>(R.id.forecastRecyclerView).let{
            it.adapter = forecastAdapter
            it.layoutManager = layoutManager
        }

        //Temponary data
        val data: ArrayList<Forecast> = ArrayList<Forecast>()
        dataLive.value = data

        //Download forecast
        var serviceForecast = retrofit.create(WeatherService::class.java)
        val callForecast = serviceForecast.getForecast(locVM.cityName.value!!, "metric", AppId)

        callForecast.enqueue(object: Callback<ForecastResponse>{
            override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Nie udalo sie pobrac danych", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ForecastResponse>,
                response: Response<ForecastResponse>
            ) {
                if (response.code() === 200) {
                    val forecastResponse = response.body()!!

                    //Pobieramy dane dla kazdego dnia
                    //Temp min
                    var i = 0
                    var temp_min: Float? = Float.MAX_VALUE
                    var temp_max: Float? = Float.MIN_VALUE
                    for(item in forecastResponse.list){
                        i++
                        if(item.main?.temp_min!! < temp_min!!){
                            temp_min = item.main?.temp_min
                        }
                        if(item.main?.temp_max!! > temp_max!!){
                            temp_max = item.main?.temp_max
                        }
                        if(i%8==0){
                            println("${item.dt_txt}")
                            println("${temp_min} - ${temp_max}")
                            when(getWeekDay(item.dt_txt!!.split(" ").get(0), Locale.getDefault())) {
                                "poniedzia??ek" -> {
                                    println("Poniedzia??ek")
                                    data.add(Forecast("Poniedzia??ek", item.weather?.get(0)?.main!!, "${temp_min!!.roundToInt()}-${temp_max!!.roundToInt()}??C"))
                                }
                                "wtorek" -> {
                                    println("Wtorek")
                                    data.add(Forecast("Wtorek", item.weather?.get(0)?.main!!, "${temp_min!!.roundToInt()}-${temp_max!!.roundToInt()}??C"))
                                }
                                "??roda" -> {
                                    println("??roda")
                                    data.add(Forecast("??roda", item.weather?.get(0)?.main!!, "${temp_min!!.roundToInt()}-${temp_max!!.roundToInt()}??C"))
                                }
                                "czwartek" -> {
                                    println("Czwartek")
                                    data.add(Forecast("Czwartek", item.weather?.get(0)?.main!!, "${temp_min!!.roundToInt()}-${temp_max!!.roundToInt()}??C"))
                                }
                                "pi??tek" -> {
                                    println("Pi??tek")
                                    data.add(Forecast("Pi??tek", item.weather?.get(0)?.main!!, "${temp_min!!.roundToInt()}-${temp_max!!.roundToInt()}??C"))
                                }
                                "sobota" -> {
                                    println("Sobota")
                                    data.add(Forecast("Sobota", item.weather?.get(0)?.main!!, "${temp_min!!.roundToInt()}-${temp_max!!.roundToInt()}??C"))
                                }
                                "niedziela" -> {
                                    println("Niedziela")
                                    data.add(Forecast("Niedziela", item.weather?.get(0)?.main!!, "${temp_min!!.roundToInt()}-${temp_max!!.roundToInt()}??C"))
                                }
                            }

                            println("${item.weather?.get(0)?.main}")
                            println("====================")
                        }
                    }
                    //Forecast:
                    //dayName -> Do policzenia z daty
                    //weatherIcon -> switch na podstawie main weather
                    //temp -> do zlozenia string z temp_min - temp_max

                    //5 dni co 3h
                    //24h/3h -> 8 update w ciagu dnia
                    //Bierzemy pogode z 4 update

                    //Czyli:
                    //startujemy od index 3
                    //+=8
                    //przelatujemy przez wszystkie
                    dataLive.value = data
                }
            }
        })

        val swipe : SwipeRefreshLayout = findViewById(R.id.refreshLayout)

        swipe.setOnRefreshListener {
            //refresh api data
            val retrofit = Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(WeatherService::class.java)
            val call = service.getCurrentWeather(locVM.cityName.value!!, "metric",
                AppId
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
                                findViewById<TextView>(R.id.weatherName).text = "OPADY ??NIEGU"
                                findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.snow)
                            }
                            "Mist" -> {
                                findViewById<TextView>(R.id.weatherName).text = "MG??A"
                                findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.foggy)
                            }
                            "Clear" -> {
                                findViewById<TextView>(R.id.weatherName).text = "S??ONECZNIE"
                                findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.sunny)
                            }
                            "Clouds" -> {
                                findViewById<TextView>(R.id.weatherName).text = "ZACHMURZENIE"
                                findViewById<ImageView>(R.id.weatherIcon).setImageResource(R.drawable.mostly_cloudy)
                            }
                        }


                        //temp. press, humidity
                        findViewById<TextView>(R.id.temp).text = "${weatherResponse!!.main!!.temp.roundToInt().toString()} ??C"
                        findViewById<TextView>(R.id.pressure).text = "${weatherResponse!!.main!!.pressure.roundToInt().toString()} hPa"
                        findViewById<TextView>(R.id.humidity).text = "${weatherResponse!!.main!!.humidity.roundToInt().toString()} %"


                        //sunrise, sunset, date

                        findViewById<TextView>(R.id.sunriseTime).text = epochToTime(weatherResponse!!.sys!!.sunrise)
                        findViewById<TextView>(R.id.sunsetTime).text = epochToTime(weatherResponse!!.sys!!.sunset)

                        val calendar = Calendar.getInstance()

                        var dateInfo = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.time)

                        println(dateInfo.split(",")[0])

                        when(dateInfo.split(",")[0]){
                            "poniedza??ek" -> {
                                findViewById<TextView>(R.id.dayName).text = "Poniedzia??ek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                            }
                            "wtorek" -> {
                                findViewById<TextView>(R.id.dayName).text = "Wtorek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                            }
                            "??roda" -> {
                                findViewById<TextView>(R.id.dayName).text = "??roda, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                            }
                            "czwartek" -> {
                                findViewById<TextView>(R.id.dayName).text = "Czwartek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
                            }
                            "pi??tek" -> {
                                findViewById<TextView>(R.id.dayName).text = "Pi??tek, ${epochToDate(weatherResponse!!.sys!!.sunrise)}"
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

            data.clear()
            dataLive.value = data
            var serviceForecast = retrofit.create(WeatherService::class.java)
            val callForecast = serviceForecast.getForecast(locVM.cityName.value!!, "metric", AppId)

            callForecast.enqueue(object: Callback<ForecastResponse>{
                override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Nie udalo sie pobrac danych", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<ForecastResponse>,
                    response: Response<ForecastResponse>
                ) {
                    if (response.code() === 200) {
                        val forecastResponse = response.body()!!

                        //Pobieramy dane dla kazdego dnia
                        //Temp min
                        var i = 0
                        var temp_min: Float? = Float.MAX_VALUE
                        var temp_max: Float? = Float.MIN_VALUE
                        for(item in forecastResponse.list){
                            i++
                            if(item.main?.temp_min!! < temp_min!!){
                                temp_min = item.main?.temp_min
                            }
                            if(item.main?.temp_max!! > temp_max!!){
                                temp_max = item.main?.temp_max
                            }
                            if(i%8==0){
                                println("${item.dt_txt}")
                                println("${temp_min} - ${temp_max}")
                                when(getWeekDay(item.dt_txt!!.split(" ").get(0), Locale.getDefault())) {
                                    "poniedzia??ek" -> {
                                        println("Poniedzia??ek")
                                        data.add(Forecast("Poniedzia??ek", item.weather?.get(0)?.main!!, "${temp_min!!.roundToInt()}-${temp_max!!.roundToInt()}??C"))
                                    }
                                    "wtorek" -> {
                                        println("Wtorek")
                                        data.add(Forecast("Wtorek", item.weather?.get(0)?.main!!, "${temp_min!!.roundToInt()}-${temp_max!!.roundToInt()}??C"))
                                    }
                                    "??roda" -> {
                                        println("??roda")
                                        data.add(Forecast("??roda", item.weather?.get(0)?.main!!, "${temp_min!!.roundToInt()}-${temp_max!!.roundToInt()}??C"))
                                    }
                                    "czwartek" -> {
                                        println("Czwartek")
                                        data.add(Forecast("Czwartek", item.weather?.get(0)?.main!!, "${temp_min!!.roundToInt()}-${temp_max!!.roundToInt()}??C"))
                                    }
                                    "pi??tek" -> {
                                        println("Pi??tek")
                                        data.add(Forecast("Pi??tek", item.weather?.get(0)?.main!!, "${temp_min!!.roundToInt()}-${temp_max!!.roundToInt()}??C"))
                                    }
                                    "sobota" -> {
                                        println("Sobota")
                                        data.add(Forecast("Sobota", item.weather?.get(0)?.main!!, "${temp_min!!.roundToInt()}-${temp_max!!.roundToInt()}??C"))
                                    }
                                    "niedziela" -> {
                                        println("Niedziela")
                                        data.add(Forecast("Niedziela", item.weather?.get(0)?.main!!, "${temp_min!!.roundToInt()}-${temp_max!!.roundToInt()}??C"))
                                    }
                                }

                                println("${item.weather?.get(0)?.main}")
                                println("====================")
                            }
                        }
                        //Forecast:
                        //dayName -> Do policzenia z daty
                        //weatherIcon -> switch na podstawie main weather
                        //temp -> do zlozenia string z temp_min - temp_max

                        //5 dni co 3h
                        //24h/3h -> 8 update w ciagu dnia
                        //Bierzemy pogode z 4 update

                        //Czyli:
                        //startujemy od index 3
                        //+=8
                        //przelatujemy przez wszystkie
                        dataLive.value = data
                    }
                }
            })

        }
        findViewById<Button>(R.id.seniorSwitch).setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity_Senior::class.java)
            //Load API data
            //getCurrentWeather(locVM.cityName.value!!)
            intent.putExtra("cityName", locVM.cityName.value)
            startActivity(intent)
            finish()
        }
    }
    private fun getWeekDay(date: String, locale: Locale): String{
        val pattern = SimpleDateFormat("yyyy-MM-dd")
        val dateParsed = pattern.parse(date)
        val formatter: DateFormat = SimpleDateFormat("EEEE", locale)
        return formatter.format(dateParsed)
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

    companion object{
        var BaseUrl = "https://api.openweathermap.org/"
        var AppId = "247efa34607080a70fe2d53cce68d206"
    }
}