package com.arrive.terminal.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RideModel(
    val id: String?,
    val pickUp: String?,
    val dropOff: String?,
    val customerPhone: String?,
    val price: Double?,
    val text: String?,
) : Parcelable