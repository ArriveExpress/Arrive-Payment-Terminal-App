package com.arrive.terminal.data.network.response;

import com.google.gson.annotations.SerializedName

class PayingTerminalEventNT(
    @SerializedName("data") val data: Data
) {

    class Data(
        @SerializedName("driver_id") val driverId: String? = null,
        @SerializedName("ride_id") val rideId: String? = null,
        @SerializedName("amount") val amount: Double? = null,
        @SerializedName("flagged_trip_id") val flaggedTripId: String? = null,
        @SerializedName("address") val address: String? = null,
        @SerializedName("breakdown") val breakdown: List<BreakdownNT>? = emptyList()
    ) {
        class BreakdownNT(
            @SerializedName("id") val id: Int? = null,
            @SerializedName("type") val type: String = "",
            @SerializedName("name") val name: String = "",
            @SerializedName("amount") val amount: Double = 0.0,
            @SerializedName("minutes") val minutes: Int? = null,
        )
    }
}