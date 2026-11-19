package com.example.planetrackermobile.data.repository

import android.util.Log
import com.example.planetrackermobile.data.model.Aircraft
import com.example.planetrackermobile.data.remote.ADSBApiService

class AircraftRepository(
    private val api: ADSBApiService
) {
    suspend fun fetchAircraftInArea(
        latitude: Double,
        longitude: Double,
        distance: Int,
    ): List<Aircraft> {
        Log.d(
            "AircraftRepository",
            "Fetching aircraft. Lat: $latitude Lon: $longitude Dist: $distance NM"
        )

        val serviceResult = api.getAircraftInArea(
            lat = latitude,
            lon = longitude,
            dist = distance
        )

        return serviceResult.ac
    }

    suspend fun fetchMilitaryAircraft(): List<Aircraft> {
        Log.d("AircraftRepository", "Fetching military aircraft")

        val serviceResult = api.getMilitaryAircraft()
        return serviceResult.ac
    }
}