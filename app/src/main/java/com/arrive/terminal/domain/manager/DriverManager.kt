package com.arrive.terminal.domain.manager

import com.arrive.terminal.domain.model.AccountModel
import com.arrive.terminal.domain.model.AdScheduleModel
import com.arrive.terminal.domain.model.CardModel
import com.arrive.terminal.domain.model.FlaggedTripModel
import com.arrive.terminal.domain.model.MainScreenModel
import com.arrive.terminal.domain.model.RideModel
import com.arrive.terminal.domain.model.WeatherModel

interface DriverManager {


    suspend fun saveDriverId(driverId: String)

    suspend fun getSavedDriverId(): String?

    suspend fun getMainScreenAuthorized(force: Boolean): Result<MainScreenModel>

    suspend fun getMainScreen(driverId: String): Result<MainScreenModel>

    suspend fun processCardPayment(
        ride: RideModel?,
        flaggedTrip: FlaggedTripModel?,
        card: CardModel
    ): Result<String?>

    suspend fun processAccountPayment(
        ride: RideModel?,
        flaggedTrip: FlaggedTripModel?,
        account: AccountModel
    ): Result<String?>

    fun getAuthorizedDriverId(): String?

    suspend fun getFeeFixed(driverId: String? = null): Double?

    suspend fun getFeePercent(driverId: String? = null): Double?

    suspend fun getIsRateEnabled(driverId: String? = null): Boolean?

    suspend fun getDefaultRate(driverId: String? = null): Int?

    suspend fun getLastWeather(driverId: String? = null): WeatherModel?

    suspend fun getLastAdSchedules(driverId: String? = null): List<AdScheduleModel>?

    suspend fun updateWeather(weather: WeatherModel)

    suspend fun updateAdSchedules(adSchedules: List<AdScheduleModel>)
}