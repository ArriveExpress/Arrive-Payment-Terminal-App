package com.arrive.terminal.core.ui.extensions;

import android.content.Intent
import android.os.Parcelable
import com.arrive.terminal.core.ui.utils.isAtLeastAndroidTiramisu

inline fun <reified T : Parcelable> Intent.parcelableOrException(key: String): T {
    return parcelable(key) ?: throw IllegalStateException("No argument for key $key")
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    isAtLeastAndroidTiramisu() -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}