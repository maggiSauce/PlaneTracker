package com.example.planetrackermobile.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AircraftMapViewModel(
    private val service: ADSBApiService
): ViewModel() {

    private val refreshInterval = 10000L
    private val _aircraft = MutableStateFlow<List<Aircraft>>(emptyList())
    val aircraft = _aircraft.asStateFlow()

    init {
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        viewModelScope.launch {
            while(true) {
                try {
                    _aircraft.value = service.getAircraftInArea(
                        lat = 53.49764590494395,
                        lon = -113.61540474272716,
                        dist = 50,
                    ).ac ?: emptyList()
                } catch (ex: Exception) {
                    Log.d("AircraftViewModel", "Error fetching data: $ex")
                }
                delay(refreshInterval)
            }
        }
    }
}