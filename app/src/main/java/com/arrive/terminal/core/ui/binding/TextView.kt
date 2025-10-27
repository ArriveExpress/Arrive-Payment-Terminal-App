package com.arrive.terminal.core.ui.binding

import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

fun TextView.bind(
    lifecycleOwner: LifecycleOwner,
    value: LiveData<String>
) {
    value.observe(lifecycleOwner) {
        if (it != null && text.toString() != it) {
            text = it
        }
    }
}