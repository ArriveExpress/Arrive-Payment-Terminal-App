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
        @SerializedName("breakdown") val breakdown: BreakdownNT? = null
    ) {

        class BreakdownNT(
            @SerializedName("Trip") val tripValue: Double? = null,
            @SerializedName("Minivan") val minivanValue: Double? = null,
            @SerializedName("Wait") val waitValue: Double? = null,
            @SerializedName("Stop") val stopValue: Double? = null,
            @SerializedName("Luggage") val luggageValue: Double? = null
        )
    }
}