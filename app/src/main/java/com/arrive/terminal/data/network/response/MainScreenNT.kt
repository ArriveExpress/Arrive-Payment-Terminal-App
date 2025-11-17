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
    @SerializedName("flagged_trip") val flaggedTripId: FlaggedTripNT? = null,
    @SerializedName("percent") val percent: Double? = null,
    @SerializedName("fixed") val fixed: Double? = null,
    @SerializedName("review_enabled") val isRateEnabled: Boolean? = null,
    @SerializedName("review_rating_default") val defaultRate: Int? = null,
    @SerializedName("weather") val weather: WeatherNT? = null,
    @SerializedName("todays_ad_schedules") val todaysAdSchedules: List<AdScheduleNT>? = null,
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

    class WeatherNT(
        @SerializedName("temperature") val temperature: Double? = null,
        @SerializedName("icon_url") val iconUrl: String? = null,
    )

    class AdScheduleNT(
        @SerializedName("multiply") val multiply: Int? = null,
        @SerializedName("click_pay_ad") val ad: AdNT? = null,
    )

    class AdNT(
        @SerializedName("id") val id: String? = null,
        @SerializedName("image_url") val imageUrl: String? = null
    )
}