package com.arrive.terminal.presentation.host

import LiveEvent
import com.arrive.terminal.core.ui.model.StringValue

interface MainHost {

    fun onChangeTitle(title: StringValue?)

    fun onShowHostProgress(visible: Boolean)

    fun subscribePaymentEvents()

    fun getWeatherUpdateEvent(): LiveEvent
}