package com.example.planetrackermobile.data

import android.app.Application
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class AircraftMapViewModel(application: Application) : AndroidViewModel(application) {

    private val DEFAULT_REFRESH_INTERVAL = 10000L
    private val settingsManager = SettingsManager(application)

    private val _aircraft = MutableStateFlow<List<Aircraft>>(emptyList())
    val aircraft = _aircraft.asStateFlow()

    init {
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        viewModelScope.launch {
            settingsManager.getRefreshInterval()
                .distinctUntilChanged()
                .flatMapLatest { interval ->

                    flow {
                        while (true) {
                            try {
                                Log.d("AircraftViewModel", "Fetching aircraftData")
                                val serviceResult = ADSBApi.retrofitService.getAircraftInArea(
                                    lat = 53.49764590494395,
                                    lon = -113.61540474272716,
                                    dist = 50,
                                )
                                emit(serviceResult.ac ?: emptyList())

                                Log.d("AircraftViewModel", "Found ${serviceResult.ac}")
                            } catch (ex: Exception) {
                                Log.d("AircraftViewModel", "Error fetching data: $ex")
                            }
                            delay(interval)
                        }
                    }
                }
                .collect { aircraft ->
                    _aircraft.value = aircraft
                }
        }
    }
}
