package com.example.pogodynka

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
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
import com.google.android.gms.location.*
import java.util.*


@Suppress("DEPRECATION")
class SplashScreen : AppCompatActivity() {

    private var PERMISSION_ID = 1000

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locVM: LocationVM
    private var isEnabled: Boolean = false

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
    }

    fun showEnableLocationSetting() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, id ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    isEnabled = true
                    getCurrentLocation()
                })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                    finish()
                })
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun continueApp(){
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
        }, 1500)
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
                continueApp()
            }else{
                if(!isEnabled){
                    showEnableLocationSetting()
                }
                else{
                    Handler().postDelayed({getCurrentLocation()}, 2000)
                }
            }
        }else{
            RequestPermission()
        }
    }
}