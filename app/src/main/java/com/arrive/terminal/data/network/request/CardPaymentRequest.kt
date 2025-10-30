package com.arrive.terminal.data.network.request;

import com.google.gson.annotations.SerializedName

/*
{
    "driver_id": 2023,
    "ride_id": 100,
    "payment_method": "credit",
    "amount": 25.50,
    "latitude": 40.712776, // not required
    "longitude": -74.005974, // not required
    "flagged_trip_id": null,
    "customer_phone": 1234567890,
    "card_number": 4242424242424242,
    "card_expiry_month": 12,
    "card_expiry_year": 2027,
    "card_cvc": 123
}
 */

class CardPaymentRequest(
    @SerializedName("driver_id") val driverId: String,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("ride_id") val rideId: String?,
    @SerializedName("amount") val amount: Double,
    @SerializedName("flagged_trip_id") val flaggedTripId: String?,
    @SerializedName("customer_phone") val customerPhone: String?,
    @SerializedName("is_card_manual_entry") val isCardManualEntry: Boolean = false,
    @SerializedName("card_number") val cardNumber: String,
    @SerializedName("card_expiry_month") val cardExpireMonth: String,
    @SerializedName("card_expiry_year") val cardExpireYear: String,
    @SerializedName("card_cvc") val cardCvc: String? = null,
    @SerializedName("card_zip_code") val cardZipCode: String? = null
)