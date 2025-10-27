package com.arrive.terminal.data.network.api;

import com.arrive.terminal.data.network.request.AccountPaymentRequest
import com.arrive.terminal.data.network.request.CardPaymentRequest
import com.arrive.terminal.data.network.response.MainScreenNT
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DriverApiService {

    @GET("$API_URL_PREFIX/terminal/main-screen/{driver_id}")
    suspend fun getMainScreen(@Path("driver_id") driverId: String): MainScreenNT

    @POST("$API_URL_PREFIX/terminal/payment")
    suspend fun processCardPayment(@Body request: CardPaymentRequest)

    @POST("$API_URL_PREFIX/terminal/payment")
    suspend fun processAccountPayment(@Body request: AccountPaymentRequest)

    companion object {

        private const val API_URL_PREFIX = "api"
    }
}