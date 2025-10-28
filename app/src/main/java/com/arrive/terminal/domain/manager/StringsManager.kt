package com.arrive.terminal.domain.manager

interface StringsManager {
    suspend fun loadRemoteStrings(): Result<Unit>

    suspend fun saveToCache(strings: Map<String, String>)

    suspend fun getCachedStrings(): Map<String, String>

    fun getString(key: String, fallback: String = ""): String
}