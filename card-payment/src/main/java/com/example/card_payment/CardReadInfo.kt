package com.example.card_payment;

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardReadInfo(
    val amount: String?,
) : Parcelable