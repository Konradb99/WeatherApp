package com.example.pogodynka.ViewModel.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.pogodynka.R
import com.example.pogodynka.model.Forecast

class ForecastAdapter(private val days: MutableLiveData<List<Forecast>>, context: Context): RecyclerView.Adapter<ForecastAdapter.MyView>() {
    class MyView(view: View) : RecyclerView.ViewHolder(view) {
        var forecastDay = view.findViewById<TextView>(R.id.forecastDay)
        var forecastTemp = view.findViewById<TextView>(R.id.forecastTemp)
        var forecastIcon = view.findViewById<ImageView>(R.id.forecastIcon)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyView {
        val itemView: View = LayoutInflater
            .from(p0.context)
            .inflate(
                R.layout.forecast_item,
                p0,
                false
            )
        return MyView(itemView)
    }

    override fun onBindViewHolder(p0: MyView, p1: Int) {
        p0.forecastDay.text = days.value?.get(p1)?.dayName
        p0.forecastTemp.text = days.value?.get(p1)?.temp

        //Icon change
        when(days.value?.get(p1)?.weatherName){
            "Thunderstorm" -> {
                p0.forecastIcon.setImageResource(R.drawable.thunderstroms)
            }
            "Drizzle" -> {
                p0.forecastIcon.setImageResource(R.drawable.showers)
            }
            "Rain" -> {
                p0.forecastIcon.setImageResource(R.drawable.showers)
            }
            "Snow" -> {
                p0.forecastIcon.setImageResource(R.drawable.snow)
            }
            "Mist" -> {
                p0.forecastIcon.setImageResource(R.drawable.foggy)
            }
            "Clear" -> {
                p0.forecastIcon.setImageResource(R.drawable.sunny)
            }
            "Clouds" -> {
                p0.forecastIcon.setImageResource(R.drawable.mostly_cloudy)
            }
            else -> {
                p0.forecastIcon.setImageResource(R.drawable.sunny)
            }
        }

    }

    override fun getItemCount()=days.value?.count()?:0
}