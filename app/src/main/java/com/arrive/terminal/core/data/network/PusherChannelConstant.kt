package com.arrive.terminal.core.data.network

const val PUSHER_CHANNEL_PAYING_TERMINAL = "paying-terminal"
const val PUSHER_CHANNEL_TERMINAL_WEATHER = "terminal-weather"
const val PUSHER_CHANNEL_AD_SCHEDULES = "click-pay-ad-schedules"

fun getPayingTerminalChannel(driverId: String): String {
    return "${PUSHER_CHANNEL_PAYING_TERMINAL}.$driverId"
}

fun getWeatherChannel(driverId: String): String {
    val driverIdInt = driverId.toIntOrNull() ?: 0
    val location = if (driverIdInt > 2000) "monroe" else "monsey"

    return "${PUSHER_CHANNEL_TERMINAL_WEATHER}.$location"
}

fun getAdSchedulesChannel(driverId: String): String {
    return "${PUSHER_CHANNEL_AD_SCHEDULES}.$driverId"
}
