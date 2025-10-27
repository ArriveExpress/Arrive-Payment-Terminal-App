package com.arrive.terminal.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreditCardModel(
    val id: String,
    val lastFour: String,
    val defaultCard: Boolean
) : Parcelable