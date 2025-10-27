package com.arrive.terminal.data.network.request;

import com.google.gson.annotations.SerializedName

/*
{
    "customer_id": 41444,
    "card_id": 2
}
 */

class SetDefaultCardRequest(
    @SerializedName("customer_id") val customerId: String,
    @SerializedName("card_id") val cardId: String
)