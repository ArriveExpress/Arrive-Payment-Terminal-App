package com.arrive.terminal.core.ui.utils

import android.util.Log

fun <T> safe(
    defaultOnError: T? = null,
    tag: String = "SAFE_CATCH_ERROR",
    block: () -> T,
): T? = try {
    block()
} catch (ignored: Throwable) {
    Log.e(tag, ignored.message ?: ignored.toString())
    defaultOnError
}

fun <T> tryWithResult(block: () -> T): Result<T> {
    return try {
        val value = block()
        Result.success(value)
    } catch (ex: Exception) {
        Result.failure(ex)
    }
}