package com.arrive.terminal.core.ui.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.arrive.terminal.core.model.ResultState
import com.arrive.terminal.core.model.dataOrNull

val <T> LiveData<List<T>>.valueOrEmpty get() = value.orEmpty()

val <T> LiveData<List<T>>.isNullOrEmptyValue get() = value.isNullOrEmpty()

val LiveData<String>.valueOrEmpty get() = value.orEmpty()

val LiveData<Boolean>.valueOrFalse get() = value.orFalse()

fun <T> LiveData<T>.observeNotNull(lifecycleOwner: LifecycleOwner, callback: (T) -> Unit) {
    observe(lifecycleOwner) {
        if (it != null) callback.invoke(it)
    }
}

fun <T, Y> LiveData<T>.mapToMutable(mapFunction: (T) -> Y): MutableLiveData<Y> {
    return MediatorLiveData<Y>().apply {
        addSource(this@mapToMutable) { x -> setValue(mapFunction.invoke(x)) }
    }
}

fun <T> MutableLiveData<T>.postValueIfNew(value: T) {
    if (this.value != value) {
        this.value = value
    }
}

fun <T> MutableLiveData<T>.updateIfNew(block: (T) -> T): Boolean {
    val newValue = value?.let(block)
    if (value != newValue) {
        value = newValue
        return true
    }
    return false
}

fun <T> MutableLiveData<ResultState<T>>.update(action: (T) -> T) {
    if (this.value is ResultState.Success) {
        this.value = ResultState.Success(action(value?.dataOrNull!!))
    }
}