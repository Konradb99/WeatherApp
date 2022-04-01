package com.example.pogodynka.ViewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class LocationVMFactory(private val application: Application, private val context: Context): ViewModelProvider.Factory{
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LocationVM::class.java)){
            return LocationVM(application, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}