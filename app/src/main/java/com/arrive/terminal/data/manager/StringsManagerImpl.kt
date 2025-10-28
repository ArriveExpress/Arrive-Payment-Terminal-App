package com.arrive.terminal.data.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.arrive.terminal.data.network.api.ConstantsApiService
import com.arrive.terminal.domain.manager.StringsManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class StringsManagerImpl @Inject constructor(
    private val api: ConstantsApiService,
    @ApplicationContext private val context: Context
) : StringsManager {

    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { context.dataStoreFile("remote_strings.preferences_pb") }
    )

    companion object {
        private val KEY_REMOTE_STRINGS_JSON = stringPreferencesKey("remote_strings_json")
    }

    private var cachedStrings: Map<String, String> = emptyMap()

    override suspend fun loadRemoteStrings(): Result<Unit> {
        return try {
            val responseJson = api.getConstants()
            val responseMap = parseRemoteStrings(responseJson.toString())
            cachedStrings = responseMap
            saveToCache(responseMap)
            Result.success(Unit)
        } catch (e: Exception) {
            val fallback = getCachedStrings()
            if (fallback.isNotEmpty()) {
                cachedStrings = fallback
                Result.success(Unit)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun saveToCache(strings: Map<String, String>) {
        val json = JSONObject(strings).toString()
        dataStore.edit { prefs ->
            prefs[KEY_REMOTE_STRINGS_JSON] = json
        }
    }

    override suspend fun getCachedStrings(): Map<String, String> {
        val prefs = dataStore.data.first()
        val json = prefs[KEY_REMOTE_STRINGS_JSON]
        return if (!json.isNullOrBlank()) {
            val jsonObject = JSONObject(json)
            jsonObject.keys().asSequence().associateWith { key -> jsonObject.getString(key) }
        } else {
            emptyMap()
        }
    }

    override fun getString(key: String, fallback: String): String {
        return cachedStrings[key] ?: fallback
    }

    private fun parseRemoteStrings(jsonArrayString: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val jsonArray = JSONArray(jsonArrayString)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val key = obj.keys().next()
            val value = obj.getString(key)
            result[key] = value
        }
        return result
    }
}

