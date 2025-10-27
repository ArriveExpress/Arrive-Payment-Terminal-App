package com.arrive.terminal.domain.model;

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MainScreenModel(
    val message: String?,
    val rides: List<RideModel>,
    val flaggedTrip: FlaggedTripModel?
) : Parcelable {

}