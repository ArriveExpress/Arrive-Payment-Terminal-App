package com.arrive.terminal.core.ui.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getDateTimeFormatted(): String {
    val dateFormat = SimpleDateFormat("EEE, MMM dd h:mm a", Locale.getDefault())
    return dateFormat.format(Date())
}