package com.example.card_payment;

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransResult(
    val manual: Boolean,
    val cardNumber: String,
    val expiryMonth: Int,
    val expireYear: Int
) : Parcelable

