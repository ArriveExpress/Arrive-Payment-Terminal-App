package com.arrive.terminal.core.ui.utils.livedata

import androidx.annotation.MainThread
import androidx.collection.ArraySet
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.arrive.terminal.core.ui.utils.isUI

open class BaseSafeLiveEvent<T> internal constructor(): LiveData<T>() {

    private val observers = ArraySet<ObserverWrapper<in T>>()
    private var holdableValue = false

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        val wrapper = ObserverWrapper(observer, holdableValue)
        observers.add(wrapper)
        super.observe(owner, wrapper)
    }

    @MainThread
    override fun observeForever(observer: Observer<in T>) {
        val wrapper = ObserverWrapper(observer, holdableValue)
        observers.add(wrapper)
        super.observeForever(wrapper)
    }

    @MainThread
    override fun removeObserver(observer: Observer<in T>) {
        val wrappedObserver = observer as? ObserverWrapper<*>
        if (wrappedObserver != null && observers.remove(wrappedObserver)) {
            super.removeObserver(observer)
            if (observers.size == 0) {
                holdableValue = false
            }
            return
        }
        val iterator = observers.iterator()
        while (iterator.hasNext()) {
            val wrapper = iterator.next()
            if (wrapper.observer == observer) {
                iterator.remove()
                super.removeObserver(wrapper)
                if (observers.size == 0) {
                    holdableValue = false
                }
                break
            }
        }
    }

    fun clearObservers() = observers.forEach {
        removeObserver(it)
    }

    @MainThread
    override fun setValue(value: T?) {
        observers.forEach { it.newValue() }
        holdableValue = observers.size == 0
        super.setValue(value)
    }

    protected open fun postValueUI(value: T?) = if (isUI()) this.setValue(value) else this.postValue(value)

    private class ObserverWrapper<T>(val observer: Observer<T>, var pending: Boolean): Observer<T> {

        override fun onChanged(value: T) {
            if (pending) {
                pending = false
                observer.onChanged(value)
            }
        }

        fun newValue() {
            pending = true
        }
    }
}