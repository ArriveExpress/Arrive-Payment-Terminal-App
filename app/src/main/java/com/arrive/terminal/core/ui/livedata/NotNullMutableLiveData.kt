package com.arrive.terminal.core.ui.livedata

import androidx.lifecycle.MutableLiveData

class NotNullMutableLiveData<T>(value: T) : MutableLiveData<T>(value) {

    override fun getValue() = super.getValue()!!
}