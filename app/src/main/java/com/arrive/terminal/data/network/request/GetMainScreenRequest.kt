package com.arrive.terminal.data.network.request;

import com.google.gson.annotations.SerializedName

/*
{
    "driver_id": "2023"
}
 */

class GetMainScreenRequest(
    @SerializedName("driver_id") val driverId: String
)