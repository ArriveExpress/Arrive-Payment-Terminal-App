package com.arrive.terminal.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FlaggedTripModel(
    val price: Double?,
    val flaggedTripId: String?,
    val isPaid: Boolean,
) : Parcelable