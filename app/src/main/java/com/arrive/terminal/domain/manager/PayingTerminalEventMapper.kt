package com.arrive.terminal.domain.manager;

import com.arrive.terminal.core.data.mappers.BaseMapper
import com.arrive.terminal.core.ui.extensions.orZero
import com.arrive.terminal.data.network.response.PayingTerminalEventNT
import com.arrive.terminal.domain.model.PayingTerminalEventModel
import com.arrive.terminal.domain.model.PayingTerminalEventModel.BreakdownItem

class PayingTerminalEventMapper(model: PayingTerminalEventNT) : BaseMapper<PayingTerminalEventNT>(model) {

    val entity by lazy {
        PayingTerminalEventModel(
            driverId = model.data.driverId.orEmpty(),
            rideId = model.data.rideId,
            flaggedTripId = model.data.flaggedTripId,
            amount = model.data.amount.orZero(),
//            pickUp = "Down streen 54 low",
//            priceDetails = listOf(
//                "Trip" to 11.0,
//                "Minivan" to 2.0,
//                "Wait" to 8.0,
//                "Stop" to 5.0,
//                "Luggage" to 10.0,
//            ).map { PayingTerminalEventModel.PriceInfo(it.first, it.second) }

            address = model.data.address,
            breakdown = model.data.breakdown?.let {
                buildList<BreakdownItem> {
                    add(BreakdownItem("Trip", it.tripValue.orZero()))
                    add(BreakdownItem("Minivan", it.minivanValue.orZero()))
                    add(BreakdownItem("Wait", it.waitValue.orZero()))
                    add(BreakdownItem("Stop", it.stopValue.orZero()))
                    add(BreakdownItem("Luggage", it.luggageValue.orZero()))
                }
            } ?: emptyList()
        )
    }
}