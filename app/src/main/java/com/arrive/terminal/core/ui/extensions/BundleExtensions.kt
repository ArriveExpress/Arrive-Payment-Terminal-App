package com.arrive.terminal.core.ui.extensions;

import android.os.Bundle
import com.arrive.terminal.core.ui.utils.safe
import com.arrive.terminal.core.ui.utils.isAtLeastAndroidTiramisu
import java.io.Serializable

inline fun <reified T : Serializable> Bundle.serializableOrNull(key: String): T? {
    return safe {
        when {
            isAtLeastAndroidTiramisu() -> getSerializable(key, T::class.java)
            else -> getSerializable(key)
        }
    } as? T
}

inline fun <reified T> Bundle.parcelableOrNull(key: String): T? {
    return safe {
        when {
            isAtLeastAndroidTiramisu() -> getParcelable(key, T::class.java)
            else -> getParcelable(key)
        }
    }
}

inline fun <reified T> Bundle.ifContains(key: String, block: (T) -> Unit) {
    val value = this.parcelableOrNull<T>(key)
    if (value != null) {
        block(value)
    }
}