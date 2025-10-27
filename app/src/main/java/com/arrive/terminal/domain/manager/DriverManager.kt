package com.arrive.terminal.domain.manager

import com.arrive.terminal.domain.model.AccountModel
import com.arrive.terminal.domain.model.CardModel
import com.arrive.terminal.domain.model.FlaggedTripModel
import com.arrive.terminal.domain.model.MainScreenModel
import com.arrive.terminal.domain.model.RideModel

interface DriverManager {

    suspend fun getMainScreenAuthorized(force: Boolean): Result<MainScreenModel>

    suspend fun getMainScreen(driverId: String): Result<MainScreenModel>

    suspend fun processCardPayment(
        ride: RideModel?,
        flaggedTrip: FlaggedTripModel?,
        card: CardModel
    ): Result<Unit>

    suspend fun processAccountPayment(
        ride: RideModel?,
        flaggedTrip: FlaggedTripModel?,
        account: AccountModel
    ): Result<Unit>

    fun getAuthorizedDriverId(): String?
}