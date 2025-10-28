package com.arrive.terminal.core.data.network

import kotlinx.coroutines.delay

suspend fun retryWithBackoff(
    retries: Int = 3,
    initialDelay: Long = 1000L,
    maxDelay: Long = 8000L,
    block: suspend () -> Result<Unit>
): Result<Unit> {
    var currentDelay = initialDelay
    repeat(retries - 1) {
        val result = block()
        if (result.isSuccess) return result
        delay(currentDelay)
        currentDelay = (currentDelay * 2).coerceAtMost(maxDelay)
    }
    return block()
}