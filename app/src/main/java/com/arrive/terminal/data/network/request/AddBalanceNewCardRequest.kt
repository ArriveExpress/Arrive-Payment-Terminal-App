package com.arrive.terminal.data.network.request;

import com.google.gson.annotations.SerializedName

/*
{
    "customer_id": 41444,
    "amount": 5,

    "new_card": true,

    "card_number": 4242424242424242,
    "card_expiry_month": 12,
    "card_expiry_year": 2027
}
 */

class AddBalanceNewCardRequest(
    @SerializedName("customer_id") val customerId: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("new_card") val newCard: Boolean,
    @SerializedName("card_number") val cardNumber: String,
    @SerializedName("card_expiry_month") val expMonth: String,
    @SerializedName("card_expiry_year") val expYear: String
)