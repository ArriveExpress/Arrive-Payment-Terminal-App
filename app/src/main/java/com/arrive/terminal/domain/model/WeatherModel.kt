package com.arrive.terminal.domain.model;

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherModel(
    val temperature: Double,
    val iconUrl: String
) : Parcelable {

}