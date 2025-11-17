package com.arrive.terminal.domain.model;

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MainScreenModel(
    val message: String?,
    val rides: List<RideModel>,
    val flaggedTrip: FlaggedTripModel?,
    val fixed: Double,
    val percent: Double,
    val isRateEnabled: Boolean?,
    val defaultRate: Int?,
    val weather: WeatherModel?,
    val todaysAdSchedules: List<AdScheduleModel>?
) : Parcelable {

}