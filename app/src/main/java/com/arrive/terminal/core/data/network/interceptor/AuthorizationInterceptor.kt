package com.arrive.terminal.core.data.network.interceptor;

import com.arrive.terminal.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = when {
            BuildConfig.DEBUG -> "6yr6CJ4(DB?\\r[f!)(9hhuUTAb_xDFQ]"
            else -> "6yr6CJ4(DB?\\r[f!)(9hhuUTAb_xDFQ]"
        }

        val authorizedRequest =  chain.request().newBuilder()
            .header(API_TOKEN_HEADER_NAME, token)
            .build()

        return chain.proceed(authorizedRequest)
    }

    companion object {

        private const val API_TOKEN_HEADER_NAME = "Api-Token"
    }
}