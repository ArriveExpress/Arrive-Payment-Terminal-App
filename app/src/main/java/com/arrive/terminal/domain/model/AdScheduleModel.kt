package com.arrive.terminal.domain.model;

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AdScheduleModel(
    val multiply: Int?,
    val ad: AdModel?
) : Parcelable {

    @Parcelize
    data class AdModel(
        val id: String?,
        val imageUrl: String?
) : Parcelable {

    }
}