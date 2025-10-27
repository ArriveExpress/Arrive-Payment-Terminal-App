package com.arrive.terminal.di.data;

import com.arrive.terminal.core.data.network.PusherClient
import com.arrive.terminal.core.data.network.interceptor.AuthorizationInterceptor
import com.arrive.terminal.core.data.network.providers.OkHttpProvider
import com.arrive.terminal.core.data.network.providers.RetrofitProvider
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Converter.Factory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    const val BASE_RETROFIT = "BASE_RETROFIT"

    private const val BASE_OKHTTP_CLIENT = "OKHTTP_CLIENT"

    @Provides
    @Singleton
    fun providePusherClient(): PusherClient {
        return PusherClient()
    }

    @Named(BASE_RETROFIT)
    @Provides
    @Singleton
    fun provideBaseRetrofit(converterFactory: Factory, @Named(BASE_OKHTTP_CLIENT) client: OkHttpClient): Retrofit {
        return RetrofitProvider.provideBaseRetrofit(converterFactory, client)
    }

    @Named(BASE_OKHTTP_CLIENT)
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpProvider.provideOkHttpClient(
            apiTokenInterceptor = AuthorizationInterceptor()
        )
    }

    @Provides
    @Singleton
    fun provideConverterFactory(): Factory {
        val gson = GsonBuilder().apply {
            serializeNulls()
        }.create()
        return GsonConverterFactory.create(gson)
    }
}