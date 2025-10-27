package com.arrive.terminal.data.network.mapper;

import com.arrive.terminal.core.data.mappers.BaseMapper
import com.arrive.terminal.core.ui.extensions.orZero
import com.arrive.terminal.data.network.response.GetCustomerResponseNT
import com.arrive.terminal.domain.model.CreditCardModel
import com.arrive.terminal.domain.model.CustomerAccountModel

class CustomerAccountMapper(model: GetCustomerResponseNT) :
    BaseMapper<GetCustomerResponseNT>(model) {

    val entity by lazy {
        CustomerAccountModel(
            id = model.customer?.id.orEmpty(),
            name = model.customer?.name.orEmpty(),
            phone = model.customer?.phone.orEmpty(),
            balance = model.customer?.balance.orZero(),
            miles = model.customer?.miles.orEmpty(),
            cards = model.customer?.cards.orEmpty().map { card ->
                CreditCardModel(
                    id = card.id.orEmpty(),
                    lastFour = card.lastFour.orEmpty(),
                    defaultCard = card.defaultCard == 1
                )
            }
        )
    }
}

