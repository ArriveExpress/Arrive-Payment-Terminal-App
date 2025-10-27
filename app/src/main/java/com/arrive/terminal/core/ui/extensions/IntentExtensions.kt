package com.arrive.terminal.core.ui.extensions

import android.content.Intent
import com.arrive.terminal.core.ui.utils.isAtLeastAndroidTiramisu
import com.arrive.terminal.core.ui.utils.safe

inline fun <reified T> Intent.parcelableOrNull(key: String): T? {
    return safe {
        when {
            isAtLeastAndroidTiramisu() -> getParcelableExtra(key, T::class.java)
            else -> getParcelableExtra(key)
        }
    }
}

inline fun <reified T : java.io.Serializable> Intent.serializableOrNull(key: String): T? {
    return safe {
        when {
            isAtLeastAndroidTiramisu() -> getSerializableExtra(key, T::class.java) as? T
            else -> getSerializableExtra(key)
        }
    } as? T
}