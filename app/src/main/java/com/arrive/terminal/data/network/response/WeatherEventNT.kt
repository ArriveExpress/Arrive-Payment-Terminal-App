package com.arrive.terminal.data.network.response;

import com.google.gson.annotations.SerializedName

class WeatherEventNT(
    @SerializedName("data") val data: Data
) {

    class Data(
        @SerializedName("temperature") val temperature: Double? = null,
        @SerializedName("icon_url") val iconUrl: String? = null,
    )
}
