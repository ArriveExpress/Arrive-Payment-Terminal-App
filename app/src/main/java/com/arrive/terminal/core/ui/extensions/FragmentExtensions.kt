package com.arrive.terminal.core.ui.extensions

import android.os.Parcelable
import android.view.View
import androidx.fragment.app.Fragment
import com.arrive.terminal.core.ui.helper.KeyboardHelper.clearFocusHideKeyboard
import kotlin.properties.ReadOnlyProperty

fun Fragment.clearFocusHideKeyboard(rootView: View) {
    activity?.window?.clearFocusHideKeyboard(rootView)
}

inline fun <reified T : Parcelable> parcelableOrNull(key: String) = ReadOnlyProperty<Fragment, T?> { thisRef, _ ->
    thisRef.requireArguments().parcelableOrNull<T>(key)
}

inline fun <reified T : Parcelable> parcelableOrException(key: String) = ReadOnlyProperty<Fragment, T> { thisRef, _ ->
    thisRef.requireArguments()
        .parcelableOrNull<T>(key)
        ?: throw IllegalArgumentException("No argument for key > $key")
}