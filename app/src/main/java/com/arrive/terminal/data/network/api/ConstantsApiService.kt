package com.arrive.terminal.data.network.api

import retrofit2.http.GET

interface ConstantsApiService {
    @GET("$API_URL_PREFIX/terminal/constants")
    suspend fun getConstants(): List<Map<String, String>>

    companion object {
        private const val API_URL_PREFIX = "api"
    }
}