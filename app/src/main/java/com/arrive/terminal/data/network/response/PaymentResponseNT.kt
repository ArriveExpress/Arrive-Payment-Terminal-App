package com.arrive.terminal.data.network.response

import com.google.gson.annotations.SerializedName

data class PaymentResponseNT(
    @SerializedName("customer_id") val customerId: String? = null
)