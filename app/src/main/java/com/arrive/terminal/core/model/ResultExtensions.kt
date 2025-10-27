package com.arrive.terminal.core.model

import com.arrive.terminal.core.data.network.AppException

inline fun <T> Result<T>.onAppException(block: (exception: AppException) -> Unit) {
    onFailure {
        if (it is AppException) block(it)
    }
}