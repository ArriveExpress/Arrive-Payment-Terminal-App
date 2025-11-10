package com.arrive.terminal.core.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.arrive.terminal.R
import com.arrive.terminal.domain.model.WeatherModel
import com.bumptech.glide.Glide

class WeatherWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val weatherIcon: ImageView
    private val weatherTemp: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_weather_widget, this, true)
        
        weatherIcon = findViewById(R.id.weatherIcon)
        weatherTemp = findViewById(R.id.weatherTemp)
    }

    fun updateWeather(weatherModel: WeatherModel) {
        val temp = weatherModel.temperature.toInt()
        weatherTemp.text = "$temp°F"

        // Load weather icon using Glide
        Glide.with(context)
            .load("https:${weatherModel.iconUrl}")
            .placeholder(R.drawable.ic_circle)
            .error(R.drawable.ic_circle)
            .into(weatherIcon)
    }
}