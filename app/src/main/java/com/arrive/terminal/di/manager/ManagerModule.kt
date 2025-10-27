package com.arrive.terminal.di.manager;

import com.arrive.terminal.data.manager.CustomerManagerImpl
import com.arrive.terminal.data.manager.DriverManagerImpl
import com.arrive.terminal.domain.manager.CustomerManager
import com.arrive.terminal.domain.manager.DriverManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface ManagerModule {

    @Binds
    @Singleton
    fun bindDriverManager(source: DriverManagerImpl): DriverManager

    @Binds
    @Singleton
    fun bindCustomerManager(source: CustomerManagerImpl): CustomerManager
}