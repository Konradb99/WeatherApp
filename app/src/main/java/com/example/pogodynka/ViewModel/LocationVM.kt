package com.example.pogodynka.ViewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LocationVM(application: Application, private val context: Context): AndroidViewModel(application) {
    var latitude = 0.0
    var longitude = 0.0

    val cityName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}