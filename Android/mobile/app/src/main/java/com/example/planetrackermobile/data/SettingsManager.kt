package com.example.planetrackermobile.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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
}

class SettingsManager(private val context: Context) {
    companion object {
        val SELECTED_THEME = stringPreferencesKey("selected_theme")
        val DARK_MODE_ENABLED = booleanPreferencesKey("darkModeEnabled")
        val UPDATE_RATE = stringPreferencesKey("updateRate")
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

    suspend fun printSettings() {
        context.dataStore.data.collect { preferences ->
            preferences.asMap().forEach { (key, value) ->
                Log.d("[DEBUG] SettingsManager", "${key.name}: $value")
            }
        }
    }
}