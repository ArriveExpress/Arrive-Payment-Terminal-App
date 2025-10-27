package com.arrive.terminal.core.data.network.providers;

import com.arrive.terminal.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit

object RetrofitProvider {

    fun provideBaseRetrofit(
        converterFactory: Converter.Factory,
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_API_URL)
            .addConverterFactory(converterFactory)
            .client(client)
            .build()
    }
}