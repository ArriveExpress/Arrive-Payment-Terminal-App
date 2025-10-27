package com.arrive.terminal.core.ui.livedata

import com.arrive.terminal.core.ui.utils.livedata.BaseSafeLiveEvent

class SafeLiveEvent<T> : BaseSafeLiveEvent<T>() {

    public override fun setValue(value: T?) = super.setValue(value)

    public override fun postValue(value: T) = super.postValue(value)

    public override fun postValueUI(value: T?) = super.postValueUI(value)
}