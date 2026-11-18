package com.example.planetrackermobile.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

const val EQUATORIAL_METRES_PER_PIXEL_AT_ZOOM_0 = 156543.03392
const val METRES_PER_NAUTICAL_MILE = 1852
const val QUARTER_WORLD_ZOOM_ZERO_NM =
    EQUATORIAL_METRES_PER_PIXEL_AT_ZOOM_0 / METRES_PER_NAUTICAL_MILE / 4

class AircraftMapViewModel(
    application: Application,
    private val imageRepository: ImageRepository,
//    aircraftRepository: AircraftRepository,
) : AndroidViewModel(application) {
    private val settingsManager = SettingsManager(application)

    private val _aircraft = MutableStateFlow<List<Aircraft>>(emptyList())
    val aircraft = _aircraft.asStateFlow()

    private val _latitude = MutableStateFlow(StaticSettings.DEFAULT_LATITUDE)
    val latitude = _latitude.asStateFlow()

    private val _longitude = MutableStateFlow(StaticSettings.DEFAULT_LONGITUDE)
    val longitude = _longitude.asStateFlow()

    private val _searchDistance = MutableStateFlow(StaticSettings.DEFAULT_SEARCH_DISTANCE)
    val searchDistance = _searchDistance.asStateFlow()

    private val _isPollingEnabled = MutableStateFlow(false)
    val isPollingEnabled = _isPollingEnabled.asStateFlow()

    private val _searchingForMilitary = MutableStateFlow(false)
    val searchingForMilitary = _searchingForMilitary.asStateFlow()

    private val _currentImageUrl = MutableStateFlow<String?>(null)
    val currentImageUrl = _currentImageUrl.asStateFlow()

    init {
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        viewModelScope.launch {
            _isPollingEnabled
//                .distinctUntilChanged()
                .flatMapLatest { enabled ->
                    if (!enabled) {
                        // halt emissions
                        return@flatMapLatest emptyFlow()
                    }

                    settingsManager.getRefreshInterval()
                        .distinctUntilChanged()
                        .combine(_searchingForMilitary) { interval, isMilitary ->
                            Pair(interval, isMilitary)
                        }
                        .flatMapLatest { (interval, isMilitary) ->
                            createApiPollingFlow(interval, _searchingForMilitary.value)
                        }
                }.collect { aircraft ->
                    _aircraft.value = aircraft
                }
        }
    }

    private fun createApiPollingFlow(
        interval: Long,
        isMilitary: Boolean
    ): Flow<List<Aircraft>> = flow {
        while (true) {
            try {
                Log.d(
                    "AircraftViewModel",
                    "Fetching aircraftData lat: ${_latitude.value} lon: ${_longitude.value} dist: ${_searchDistance.value} isMilitary: ${_searchingForMilitary.value}"
                )

                val aircraft = fetchAircraft(
                    _latitude.value,
                    _longitude.value,
                    _searchDistance.value,
                    isMilitary
                )
                emit(aircraft)

                Log.d("AircraftViewModel", "Found ${aircraft.size} $aircraft")
            } catch (ex: Exception) {
                Log.d("AircraftViewModel", "Error fetching data: $ex")
            }
            delay(interval.milliseconds)
        }
    }


    /// API CALLS
//    private suspend fun fetchAircraft(
//        latitude: Double,
//        longitude: Double,
//        distance: Int,
//        isMilitary: Boolean
//    ): List<Aircraft> {
//        val serviceResult = if (isMilitary) {
//            PlaneTrackerAPIService.adsbService.getMilitaryAircraft()
//        } else {
//            PlaneTrackerAPIService.adsbService.getAircraftInArea(
//                lat = latitude,
//                lon = longitude,
//                dist = distance
//            )
//        }
//
//        return serviceResult.ac ?: emptyList()
//    }
//
//    private suspend fun fetchAircraftImage(registration: String): List<ImageResponse> {
//        Log.d("AircraftViewModel", "Fetching aircraft image for registration: $registration")
//
//        val jetApiResponse =
//            PlaneTrackerAPIService.jetService.getAircraftPhotos(registration = registration)
//
//        Log.d("AircraftViewModel", "$jetApiResponse")
//
//        return jetApiResponse.images ?: emptyList()
//    }

    /*
     * Updates the search location parameters to be passed onto getAircraftInArea calls
     * Called when user moves/manipulates the map camera
     */
    fun updateSearchLocation(lat: Double, lon: Double, zoom: Double, mapWidth: Int) {
        var radiusNM =
            ((QUARTER_WORLD_ZOOM_ZERO_NM * cos(Math.toRadians(lat)) * mapWidth) / (2.0).pow(zoom)).roundToInt()
        if (radiusNM <= 0) {
            radiusNM = 5;
        } else if (radiusNM > 230) {
            radiusNM = 230
        }

        _latitude.value = lat;
        _longitude.value = lon;
        _searchDistance.value = radiusNM;
        Log.d("AircraftViewModel", "Search location updated ($lon, $lat) $radiusNM NM zoom: $zoom")
    }

    fun startPolling() {
        _isPollingEnabled.value = true
        Log.d("AircraftViewModel", "Started Polling")
    }

    fun stopPolling() {
        _isPollingEnabled.value = false
        Log.d("AircraftViewModel", "Stopped Polling")
    }

    fun toggleSearchForMilitary() {
        _searchingForMilitary.value = !searchingForMilitary.value
        Log.d("AircraftViewModel", "Searching for military: ${_searchingForMilitary.value}")
    }

    fun loadAircraftImage(registration: String?) {
        viewModelScope.launch {
            _currentImageUrl.value = imageRepository.getAircraftPhotoUri(registration)
        }
    }
}
