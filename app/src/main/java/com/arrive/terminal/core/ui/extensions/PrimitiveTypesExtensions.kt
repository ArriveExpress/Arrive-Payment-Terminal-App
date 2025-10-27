package com.arrive.terminal.core.ui.extensions

import java.math.BigDecimal

fun Boolean?.orFalse() = this == true

fun Int?.orZero() = this ?: 0

fun Int?.orValue(value: Int) = this ?: value

fun Double?.orZero() = this ?: 0.0

fun Double?.asBigDecimalOrNull(): BigDecimal? {
    return this?.let { BigDecimal.valueOf(it) }
}

fun <E : Enum<*>> E.equalsOrNull(to: E): Boolean? {
    return if (this == to) true else null
}

fun <K, E : Enum<*>> Map<K, E>.getKeyName(value: E): K? {
    return this.filterValues { it == value }.keys.firstOrNull()
}