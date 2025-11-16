package com.arrive.terminal.data.network.response;

import com.google.gson.annotations.SerializedName

class AdSchedulesEventNT(
    @SerializedName("data") val data: List<AdScheduleModel>
) {

    class AdScheduleModel(
        @SerializedName("multiply") val multiply: Int? = null,
        @SerializedName("click_pay_ad") val ad: AdModel? = null,
    )

    class AdModel(
        @SerializedName("id") val id: String? = null,
        @SerializedName("image_url") val imageUrl: String? = null,
    )
}
