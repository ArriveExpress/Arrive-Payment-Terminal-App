package com.arrive.terminal.core.ui.extensions

import android.webkit.URLUtil

fun String?.orDefaultIfNullOrBlank(default: String): String {
    return takeIf { !it.isNullOrBlank() } ?: default
}

fun String?.takeIfNotBlank(): String? {
    return takeIf { !it.isNullOrBlank() }
}

fun String?.containsWhitespace(): Boolean {
    return !isNullOrBlank() && any { it.isWhitespace() }
}

fun String?.takeIfNotNullOrBlank(): String? {
    return takeIf { !it.isNullOrBlank() }
}

fun String?.isValidUrl(): Boolean {
    return !this.isNullOrBlank() && URLUtil.isValidUrl(this)
}

fun String?.ifNotNullOrBlank(block: (String) -> Unit) {
    if (!isNullOrBlank()) {
        block(this)
    }
}