package com.arrive.terminal.core.ui.binding

import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

fun View.bindVisibility(
    lifecycleOwner: LifecycleOwner,
    value: LiveData<Boolean>
) {
    value.observe(lifecycleOwner) {
        isVisible = it == true
    }
}