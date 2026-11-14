package com.example.planetrackermobile.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsOptions {
    // find more maps at https://makewithmaplibre.com/basemaps/
    val themesMap: Map<String, String> = mapOf(
        "FIORD" to "https://tiles.openfreemap.org/styles/fiord",
        "LIBERTY" to "https://tiles.openfreemap.org/styles/liberty",
        "DARK" to "https://styles.maptoolkit.org/dark.json",
    )
    val updateRates = listOf("SLOW", "NORMAL", "FAST")
    val searchRangeIcons = listOf("CLOSE", "NORMAL", "FAR")
}

class SettingsManager(private val context: Context) {
    companion object {
        val SELECTED_THEME = stringPreferencesKey("selected_theme")
        val DARK_MODE_ENABLED = booleanPreferencesKey("darkModeEnabled")
        val UPDATE_RATE = stringPreferencesKey("updateRate")
        val SEARCH_RANGE_IN_NMs = intPreferencesKey("searchRangeInNMs")
        val SEARCH_RANGE_ICON = stringPreferencesKey("searchRangeIcon")
    }

    suspend fun saveString(key: Preferences.Key<String>, value: String) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun saveBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun saveInt(key: Preferences.Key<Int>, value: Int) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    fun getString(key: Preferences.Key<String>, defaultValue: String): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    fun getBoolean(key: Preferences.Key<Boolean>, defaultValue: Boolean): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    fun getInt(key: Preferences.Key<Int>, defaultValue: Int): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    suspend fun printSettings() {
        context.dataStore.data.collect { preferences ->
            preferences.asMap().forEach { (key, value) ->
                Log.d("[DEBUG] SettingsManager", "${key.name}: $value")
            }
        }
    }

    // custom getters
    fun getRefreshInterval(): Flow<Long> {
        return getString(
            UPDATE_RATE,
            "NORMAL"
        ).map { rate ->
            when (rate) {
                "SLOW" -> 30000L
                "NORMAL" -> 5000L
                "FAST" -> 1000L
                else -> 5000L
            }
        }
    }
}