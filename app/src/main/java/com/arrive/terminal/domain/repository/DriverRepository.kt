package com.arrive.terminal.domain.repository;

import com.arrive.terminal.domain.model.AccountModel
import com.arrive.terminal.domain.model.CardModel
import com.arrive.terminal.domain.model.FlaggedTripModel
import com.arrive.terminal.domain.model.MainScreenModel
import com.arrive.terminal.domain.model.RideModel

interface DriverRepository {

    suspend fun getMainScreen(driverId: String): Result<MainScreenModel>

    suspend fun processCardPayment(
        driverId: String,
        ride: RideModel?,
        flaggedTrip: FlaggedTripModel?,
        card: CardModel
    ): Result<String?>

    suspend fun processAccountPayment(
        driverId: String,
        ride: RideModel?,
        flaggedTrip: FlaggedTripModel?,
        account: AccountModel
    ): Result<String?>
}