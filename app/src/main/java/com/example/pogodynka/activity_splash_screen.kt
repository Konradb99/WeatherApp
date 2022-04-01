package com.example.pogodynka

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.pogodynka.ViewModel.LocationVM
import com.example.pogodynka.ViewModel.LocationVMFactory
import com.example.pogodynka.model.WeatherResponse
import com.example.pogodynka.model.WeatherService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

@Suppress("DEPRECATION")
class SplashScreen : AppCompatActivity() {

    private var PERMISSION_ID = 1000

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    lateinit var locVM: LocationVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // This is used to hide the status bar and make
        // the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val logo : ImageView = findViewById(R.id.SplashScreenImage)
        val sideAnim = AnimationUtils.loadAnimation(this, R.anim.slide_loading)
        logo.startAnimation(sideAnim)


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val factoryCardVM = LocationVMFactory((requireNotNull(this).application), this.applicationContext)
        locVM = ViewModelProvider(this, factoryCardVM).get(LocationVM::class.java)
        getCurrentLocation()

        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        Handler().postDelayed({
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(locVM.latitude, locVM.longitude, 1)
            val cityName = addresses[0].locality
            val intent = Intent(this@SplashScreen, MainActivity::class.java)
            //Load API data
            //getCurrentWeather(locVM.cityName.value!!)

            intent.putExtra("cityName", cityName)
            startActivity(intent)
            finish()
        }, 1500) // 3000 is the delayed time in milliseconds.



    }


    private fun CheckPermission():Boolean{
        if(
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }else{
            return false
        }


    }

    private fun RequestPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            PERMISSION_ID
        )
    }

    private fun isLocationEnabled():Boolean{
        var locationMananger = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationMananger.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationMananger.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Debug: ", "You have permission")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(){
        if(CheckPermission()){
            if(isLocationEnabled()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){task->
                    val location: Location?=task.result
                    if(location == null){
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    }else{
                        println("=========================")
                        println(location.latitude)
                        println(location.longitude)
                        locVM.longitude = location.longitude
                        locVM.latitude = location.latitude
                    }
                }

            }else{
                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show()
            }
        }else{
            RequestPermission()
        }
    }

    companion object {
        var BaseUrl = "https://api.openweathermap.org/"
        var AppId = "247efa34607080a70fe2d53cce68d206"
    }
}