package com.arrive.terminal.data.network.request;

import com.google.gson.annotations.SerializedName

/*
{
    "driver_id": 2023,
    "ride_id": 101,
    "payment_method": "account",
    "amount": 30.00,
    "latitude": 34.052235, // not required
    "longitude": -118.243683, // not required
    "flagged_trip_id": 5,
    "customer_phone": 9876543210,
    "account_number": 11111111111,
    "account_pin": 1234
}
 */

class AccountPaymentRequest(
    @SerializedName("driver_id") val driverId: String,
    @SerializedName("ride_id") val rideId: String?,
    @SerializedName("amount") val amount: Double,
    @SerializedName("flagged_trip_id") val flaggedTripId: String?,
    @SerializedName("customer_phone") val customerPhone: String?,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("account_number") val accountNumber: String,
    @SerializedName("account_pin") val accountPin: String,
)