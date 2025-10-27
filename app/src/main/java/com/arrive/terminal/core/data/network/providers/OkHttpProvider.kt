package com.arrive.terminal.core.data.network.providers

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object OkHttpProvider {

    private const val CONNECT_TIMEOUT = 60L
    private const val WRITE_TIMEOUT = 60L
    private const val READ_TIMEOUT = 60L

    fun provideOkHttpClient(
        apiTokenInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(getHttpLoggingInterceptor())
            addInterceptor(apiTokenInterceptor)
            connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        }.build()
    }

    private fun getHttpLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}