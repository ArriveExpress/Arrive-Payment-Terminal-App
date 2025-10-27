package com.arrive.terminal.data.network.response;

import com.google.gson.annotations.SerializedName

/*
{
    "message": "Driver initialized successfully",
    "rides": [
        {
            "id": 3188998,
            "pick_up": "59 Forest Road, Kiryas Joel, NY, USA",
            "drop_off": "791 NY-17M, Monroe, NY 10950, EE. UU.",
            "customer_phone": "18452639769",
            "price": 1,
            "text": "Ride for ***-***-9769"
        },
    ],
    "flagged_trip": {
        "price": 2,
        "flagged_trip_id": 18,
        "is_paid": false
    }
}
 */

class MainScreenNT(
    @SerializedName("message") val message: String? = null,
    @SerializedName("rides") val rides: List<RideNT>? = null,
    @SerializedName("flagged_trip") val flaggedTripId: FlaggedTripNT? = null
) {

    class RideNT(
        @SerializedName("id") val id: String? = null,
        @SerializedName("pick_up") val pickUp: String? = null,
        @SerializedName("drop_off") val dropOff: String? = null,
        @SerializedName("customer_phone") val customerPhone: String? = null,
        @SerializedName("price") val price: Double? = null,
        @SerializedName("text") val text: String? = null,
    )

    class FlaggedTripNT(
        @SerializedName("price") val price: Double? = null,
        @SerializedName("flagged_trip_id") val flaggedTripId: String? = null,
        @SerializedName("is_paid") val isPaid: Boolean? = null,
    )
}