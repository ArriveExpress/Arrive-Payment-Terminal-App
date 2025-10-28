package com.arrive.terminal.domain.model;

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PayingTerminalEventModel(
    val driverId: String,
    val rideId: String?,
    val amount: Double,
    val flaggedTripId: String? = null,
    val address: String?,
    val breakdown: List<BreakdownItem>? = emptyList(),
) : Parcelable {

    @Parcelize
    class BreakdownItem(
        val title: String,
        val value: Double,
        val minutes: Int? = null
    ) : Parcelable
}

val PayingTerminalEventModel.isRide: Boolean
    get() = rideId != null

val PayingTerminalEventModel.isFlaggedTrip: Boolean
    get() = flaggedTripId != null

val PayingTerminalEventModel.ride: RideModel
    get() = RideModel(
        id = rideId,
        price = amount,
        pickUp = null,
        dropOff = null,
        customerPhone = null,
        text = null
    )

val PayingTerminalEventModel.flaggedTrip: FlaggedTripModel
    get() = FlaggedTripModel(
        flaggedTripId = flaggedTripId,
        price = amount,
        isPaid = false
    )