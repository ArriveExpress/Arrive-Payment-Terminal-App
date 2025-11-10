package com.arrive.terminal.domain.manager;

import com.arrive.terminal.core.data.mappers.BaseMapper
import com.arrive.terminal.data.network.response.WeatherEventNT
import com.arrive.terminal.domain.model.WeatherModel

class WeatherEventMapper(model: WeatherEventNT) : BaseMapper<WeatherEventNT>(model) {

    val entity: WeatherModel? by lazy {
        if (model.data.temperature != null && model.data.iconUrl != null) {
            WeatherModel(
                temperature = model.data.temperature,
                iconUrl = model.data.iconUrl
            )
        } else {
            null
        }
    }
}