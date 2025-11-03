package com.arrive.terminal.data.network.request;

import com.google.gson.annotations.SerializedName

/*
{
    "customer_id": 147805,
    "card_number": 4242424242424242,
    "card_expiry_month": 12,
    "card_expiry_year": 2027
}
 */

class SaveCreditCardRequest(
    @SerializedName("customer_id") val customerId: String,
    @SerializedName("is_card_manual_entry") val isManualEntry: Boolean = false,
    @SerializedName("card_number") val cardNumber: String,
    @SerializedName("card_expiry_month") val expiryMonth: String,
    @SerializedName("card_expiry_year") val expiryYear: String,
    @SerializedName("card_cvc") val cvc: String? = null
)