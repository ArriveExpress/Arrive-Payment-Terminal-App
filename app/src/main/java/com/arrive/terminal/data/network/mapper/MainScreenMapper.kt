package com.arrive.terminal.data.network.mapper;

import com.arrive.terminal.core.data.mappers.BaseMapper
import com.arrive.terminal.data.network.response.MainScreenNT
import com.arrive.terminal.domain.model.AdScheduleModel
import com.arrive.terminal.domain.model.FlaggedTripModel
import com.arrive.terminal.domain.model.MainScreenModel
import com.arrive.terminal.domain.model.RideModel
import com.arrive.terminal.domain.model.WeatherModel

class MainScreenMapper(model: MainScreenNT) : BaseMapper<MainScreenNT>(model) {

    val entity by lazy {
        MainScreenModel(
            message = model.message,
            rides = model.rides.orEmpty().map { ride ->
                RideModel(
                    id = ride.id,
                    pickUp = ride.pickUp,
                    dropOff = ride.dropOff,
                    customerPhone = ride.customerPhone,
                    price = ride.price?.takeIf { it != 0.0 },
                    text = ride.text
                )
            },
            flaggedTrip = FlaggedTripModel(
                price = model.flaggedTripId?.price?.takeIf { it != 0.0 },
                flaggedTripId = model.flaggedTripId?.flaggedTripId,
                isPaid = model.flaggedTripId?.isPaid ?: false
            ),
            fixed = model.fixed ?: 0.0,
            percent = model.percent ?: 0.0,
            isRateEnabled = model.isRateEnabled ?: false,
            defaultRate = model.defaultRate,
            weather = model.weather?.let { weatherNT ->
                // Only create WeatherModel if both temperature and iconUrl are present
                if (weatherNT.temperature != null && !weatherNT.iconUrl.isNullOrBlank()) {
                    WeatherModel(
                        temperature = weatherNT.temperature,
                        iconUrl = weatherNT.iconUrl
                    )
                } else {
                    null
                }
            },
            todaysAdSchedules = model.todaysAdSchedules.orEmpty().map { adScheduleNT ->
                AdScheduleModel(
                    multiply = adScheduleNT.multiply,
                    ad = adScheduleNT.ad?.let { adNT ->
                        AdScheduleModel.AdModel(
                            id = adNT.id,
                            imageUrl = adNT.imageUrl
                        )
                    }
                )
            }
        )
    }
}