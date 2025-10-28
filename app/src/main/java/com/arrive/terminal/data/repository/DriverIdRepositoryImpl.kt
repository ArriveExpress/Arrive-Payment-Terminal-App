package com.arrive.terminal.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.arrive.terminal.domain.repository.DriverIdRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject


private val Context.dataStore by preferencesDataStore(name = "driver_preferences")

class DriverIdRepositoryImpl @Inject constructor(@ApplicationContext private val context: Context) : DriverIdRepository {

    private val DRIVER_ID_KEY = stringPreferencesKey("driver_id")

    override suspend fun saveDriverId(driverId: String) {
        context.dataStore.edit { preferences ->
            preferences[DRIVER_ID_KEY] = driverId
        }
    }

    override suspend fun getDriverId(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[DRIVER_ID_KEY]
        }.firstOrNull()
    }
}