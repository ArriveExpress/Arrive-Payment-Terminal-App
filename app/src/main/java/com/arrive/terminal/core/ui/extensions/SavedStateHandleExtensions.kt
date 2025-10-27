package com.arrive.terminal.core.ui.extensions

import androidx.lifecycle.SavedStateHandle

fun <T> SavedStateHandle.getOrException(name: String): T {
    if (contains(name).not()) {
        throw IllegalStateException("No argument named $name")
    }

    return try {
        get<T>(name)!!
    } catch (ex: java.lang.Exception) {
        throw IllegalStateException("Argument $name has another type")
    }
}

fun <T> SavedStateHandle.getOrNull(name: String): T? {
    if (contains(name).not()) {
        return null
    }

    return get<T>(name)
}
