package com.arrive.terminal.core.data.network

const val PUSHER_CHANNEL_PAYING_TERMINAL = "paying-terminal"

fun getPayingTerminalChannel(driverId: String): String {
    return "${PUSHER_CHANNEL_PAYING_TERMINAL}.$driverId"
}
