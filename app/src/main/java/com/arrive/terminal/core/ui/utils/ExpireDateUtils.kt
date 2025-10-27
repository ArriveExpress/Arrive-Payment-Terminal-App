package com.arrive.terminal.core.ui.utils

val EXPIRY_DATE_PATTERN = Regex("^(0[1-9]|1[0-2])\\s*/\\s*(\\d{2})$")

/**
 * Function to validate if the string matches the expiry date format
 * @param expiryDate The string to validate
 * @return Boolean indicating if the format is valid
 */
fun isValidExpiryDateFormat(expiryDate: String): Boolean {
    return EXPIRY_DATE_PATTERN.matches(expiryDate)
}

fun String.getExpiryDateRaw(): Pair<Int, Int>? {
    val text = this
    val digitsOnly = text.replace(" ", "").replace("/", "")

    return if (digitsOnly.length == 4) {
        try {
            val month = digitsOnly.substring(0, 2).toInt()
            val twoDigitYear = digitsOnly.substring(2, 4).toInt()
            Pair(month, 2000 + twoDigitYear)
        } catch (e: Exception) {
            null
        }
    } else {
        null
    }
}