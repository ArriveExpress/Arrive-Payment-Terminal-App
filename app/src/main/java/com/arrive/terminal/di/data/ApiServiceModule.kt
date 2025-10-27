package com.arrive.terminal.di.data;

import com.arrive.terminal.data.network.api.CustomerApiService
import com.arrive.terminal.data.network.api.DriverApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiServiceModule {

    @Singleton
    @Provides
    fun provideDriverApiService(@Named(NetworkModule.BASE_RETROFIT) retrofit: Retrofit): DriverApiService {
        return retrofit.create(DriverApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideCustomerApiService(@Named(NetworkModule.BASE_RETROFIT) retrofit: Retrofit): CustomerApiService {
        return retrofit.create(CustomerApiService::class.java)
    }
}