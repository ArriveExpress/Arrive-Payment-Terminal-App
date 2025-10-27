package com.arrive.terminal.data.network.request;

import com.google.gson.annotations.SerializedName

/*
{
    "phone": 11111111111,
    "pin": 2222
}
 */

class GetAccountRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("pin") val pin: String
)