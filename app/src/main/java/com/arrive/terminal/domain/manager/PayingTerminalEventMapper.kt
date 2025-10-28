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
            address = model.data.address,
            breakdown = model.data.breakdown?.map { item ->
                BreakdownItem(
                    item.name,
                    item.amount,
                    if (item.type == "wait_fee") item.minutes else null
                )
            }
        )
    }
}