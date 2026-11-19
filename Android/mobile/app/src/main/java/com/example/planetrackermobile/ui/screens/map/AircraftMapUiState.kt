package com.example.planetrackermobile.ui.screens.map

import com.example.planetrackermobile.data.model.Aircraft
import com.example.planetrackermobile.data.model.StaticSettings

data class AircraftMapUiState(
    val aircraft: List<Aircraft> = emptyList(),
    val latitude: Double = StaticSettings.DEFAULT_LATITUDE,
    val longitude: Double = StaticSettings.DEFAULT_LONGITUDE,
    val searchDistance: Int = StaticSettings.DEFAULT_SEARCH_DISTANCE,
    val isPollingEnabled: Boolean = false,
    val searchingForMilitary: Boolean = false,
    val currentImageUrl: String? = null
)