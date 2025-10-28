package com.arrive.terminal.core.ui.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

fun getPriceFormatted(
    amount: Double,
    withFee: Boolean = false,
    feeIndex: Double = 0.0,
    additionalFee: Double = 0.0
): String {
    return if (withFee) {
        val indexedAmount = (BigDecimal(amount) * BigDecimal(convertToCustomDouble(feeIndex))).plus(
            BigDecimal(additionalFee)
        ).setScale(2, RoundingMode.HALF_UP)
        indexedAmount.toDouble()
    } else {
        amount
    }.let { formatPrice(it) }
}

private fun convertToCustomDouble(value: Double): Double {
    return try {
        value / 100 + 1
    } catch (ex: Exception) {
        1.0
    }
}

fun formatPrice(amount: Double): String {
    val decimalFormat = DecimalFormat("$###,###.00")
    return decimalFormat.format(amount)
}