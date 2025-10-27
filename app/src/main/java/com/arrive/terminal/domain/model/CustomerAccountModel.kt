package com.arrive.terminal.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomerAccountModel(
    val id: String,
    val name: String,
    val phone: String,
    val balance: Double,
    val miles: String,
    val cards: List<CreditCardModel>
) : Parcelable {

}
