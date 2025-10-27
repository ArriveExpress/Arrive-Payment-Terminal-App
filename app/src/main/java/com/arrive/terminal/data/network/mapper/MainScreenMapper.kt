package com.arrive.terminal.data.network.mapper;

import com.arrive.terminal.core.data.mappers.BaseMapper
import com.arrive.terminal.data.network.response.MainScreenNT
import com.arrive.terminal.domain.model.FlaggedTripModel
import com.arrive.terminal.domain.model.MainScreenModel
import com.arrive.terminal.domain.model.RideModel

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
            )
        )
    }
}