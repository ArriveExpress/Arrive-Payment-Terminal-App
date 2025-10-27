package com.arrive.terminal.core.ui.binding

import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

typealias OnValueChanged<T> = (T) -> Unit

fun EditText.bindTwoWays(
    lifecycleOwner: LifecycleOwner,
    value: MutableLiveData<String>,
    onValueChanged: OnValueChanged<String>? = { value.value = it }
) {
    val watcher = doOnTextChanged { text, _, _, _ ->
        onValueChanged?.invoke(text.toString())
    }

    value.observe(lifecycleOwner) {
        if (text.toString() != it) {
            setText(it.orEmpty())
        }
    }

    lifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
        if (event == ON_DESTROY) removeTextChangedListener(watcher)
    })
}