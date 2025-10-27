package com.arrive.terminal.core.ui.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

fun <T> mapToMediator(vararg sources: LiveData<*>, onChange: () -> T): MediatorLiveData<T> {
    return MediatorLiveData<T>().apply {
        sources.forEach {
            addSource(it) { value = onChange() }
        }
    }
}

inline fun withProgress(progress: MutableLiveData<Boolean>?, block: () -> Unit) {
    if (progress?.value != true) {
        progress?.value = true
    }
    block.invoke()
    progress?.value = false
}