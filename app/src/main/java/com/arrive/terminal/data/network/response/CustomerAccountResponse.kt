package com.arrive.terminal.data.network.response;

import com.google.gson.annotations.SerializedName

/*
{
    "message": "Customer found",
    "state": "success",
    "customer": {
        "id": 147805,
        "name": "Election 2024",
        "phone": "11111111111",
        "balance": -7155,
        "miles": 0,
        "cards": [
            {
                "id": 2986,
                "last_four": "4242",
                "default": 0
            }
        ]
    }
}
 */

data class GetCustomerResponseNT(
    @SerializedName("message") val message: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("customer") val customer: CustomerNT? = null
)

data class CustomerNT(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("balance") val balance: Double? = null,
    @SerializedName("miles") val miles: String? = null,
    @SerializedName("cards") val cards: List<CardNT>? = null
)

data class CardNT(
    @SerializedName("id") val id: String? = null,
    @SerializedName("last_four") val lastFour: String? = null,
    @SerializedName("default") val defaultCard: Int? = null
)
