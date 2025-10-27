package com.arrive.terminal.core.ui.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

const val AMOUNT_CARD_CHARGE_INDEX = 1.06
const val AMOUNT_ACCOUNT_REFILL_CHARGE_INDEX = 1.03

fun getPriceFormatted(
    amount: Double,
    withFee: Boolean = false,
    feeIndex: Double = AMOUNT_CARD_CHARGE_INDEX
): String {
    return if (withFee) {
        val indexedAmount = (BigDecimal(amount) * BigDecimal(feeIndex)).setScale(2, RoundingMode.HALF_UP)
        indexedAmount.toDouble()
    } else {
        amount
    }.let { formatPrice(it) }
}

fun formatPrice(amount: Double): String {
    val decimalFormat = DecimalFormat("$###,###.00")
    return decimalFormat.format(amount)
}