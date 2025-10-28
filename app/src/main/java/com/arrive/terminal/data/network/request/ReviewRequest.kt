package com.arrive.terminal.data.network.request

import com.google.gson.annotations.SerializedName

data class ReviewRequest(
    @SerializedName("customer_id") val customerId: String?,
    @SerializedName("user_id") val userId: String,
    @SerializedName("rate") val rate: Int,
)