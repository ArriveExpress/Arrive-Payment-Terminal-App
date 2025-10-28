package com.arrive.terminal.data.network.api

import com.arrive.terminal.data.network.request.AddBalanceExistingCardRequest
import com.arrive.terminal.data.network.request.AddBalanceNewCardRequest
import com.arrive.terminal.data.network.request.GetAccountRequest
import com.arrive.terminal.data.network.request.ReviewRequest
import com.arrive.terminal.data.network.request.SaveCreditCardRequest
import com.arrive.terminal.data.network.request.SetDefaultCardRequest
import com.arrive.terminal.data.network.response.GetCustomerResponseNT
import retrofit2.http.Body
import retrofit2.http.POST

interface CustomerApiService {

    @POST("$API_URL_PREFIX/terminal/profile")
    suspend fun getCustomerProfile(@Body request: GetAccountRequest): GetCustomerResponseNT

    @POST("$API_URL_PREFIX/terminal/default-card")
    suspend fun setDefaultCard(@Body request: SetDefaultCardRequest)

    @POST("$API_URL_PREFIX/terminal/save-card")
    suspend fun saveCreditCard(@Body request: SaveCreditCardRequest)

    @POST("$API_URL_PREFIX/terminal/add-balance")
    suspend fun addBalanceExisting(@Body request: AddBalanceExistingCardRequest)

    @POST("$API_URL_PREFIX/terminal/add-balance")
    suspend fun addBalanceNew(@Body request: AddBalanceNewCardRequest)

    @POST("$API_URL_PREFIX/terminal/review")
    suspend fun setReview(@Body request: ReviewRequest)

    companion object {

        private const val API_URL_PREFIX = "api"
    }
}