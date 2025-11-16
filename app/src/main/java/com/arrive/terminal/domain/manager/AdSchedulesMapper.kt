package com.arrive.terminal.domain.manager;

import com.arrive.terminal.core.data.mappers.BaseMapper
import com.arrive.terminal.data.network.response.AdSchedulesEventNT
import com.arrive.terminal.domain.model.AdScheduleModel
import com.arrive.terminal.domain.model.AdScheduleModel.AdModel

class AdSchedulesMapper(model: AdSchedulesEventNT) : BaseMapper<AdSchedulesEventNT>(model) {

    val entity: List<AdScheduleModel> by lazy {
        model.data.map { schedule ->
            AdScheduleModel(
                multiply = schedule.multiply,
                ad = schedule.ad?.let { adModel ->
                    AdModel(
                        id = adModel.id,
                        imageUrl = adModel.imageUrl
                    )
                }
            )
        }
    }
}