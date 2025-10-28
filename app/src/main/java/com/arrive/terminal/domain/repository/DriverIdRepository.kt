package com.arrive.terminal.domain.repository

interface DriverIdRepository {
    suspend fun saveDriverId(driverId: String)
    suspend fun getDriverId(): String?
}