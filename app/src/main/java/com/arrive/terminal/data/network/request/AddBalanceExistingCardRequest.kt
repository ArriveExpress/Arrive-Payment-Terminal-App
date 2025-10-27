package com.arrive.terminal.data.network.request;

import com.google.gson.annotations.SerializedName

/*
{
    "customer_id": 41444,
    "amount": 5,
    "new_card": false,
    "card_id": 2055
}
 */

class AddBalanceExistingCardRequest(
    @SerializedName("customer_id") val customerId: String,
    @SerializedName("card_id") val cardId: String,
    @SerializedName("new_card") val newCard: Boolean,
    @SerializedName("amount") val amount: Double
)