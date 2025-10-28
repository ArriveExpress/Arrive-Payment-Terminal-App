package com.arrive.terminal.di.repository;

import com.arrive.terminal.data.repository.CustomerRepositoryImpl
import com.arrive.terminal.data.repository.DriverIdRepositoryImpl
import com.arrive.terminal.data.repository.DriverRepositoryImpl
import com.arrive.terminal.domain.repository.CustomerRepository
import com.arrive.terminal.domain.repository.DriverIdRepository
import com.arrive.terminal.domain.repository.DriverRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindDriverRepository(source: DriverRepositoryImpl): DriverRepository

    @Binds
    @Singleton
    fun bindDriverIdRepository(source: DriverIdRepositoryImpl): DriverIdRepository

    @Binds
    @Singleton
    fun bindCustomerRepository(source: CustomerRepositoryImpl): CustomerRepository
}