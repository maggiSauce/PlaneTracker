package com.example.planetrackermobile.data.model

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import org.maplibre.android.geometry.LatLng

class AppLocationManager(private val context: Context, private val onLocationChanged: (LatLng) -> Unit) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager;
    private val locationListener = LocationListener { location ->
        onLocationChanged(LatLng(location.latitude, location.longitude))
    }

    @SuppressLint("MissingPermission")
    fun startTracking() {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            3000L, // every 3 seconds
            5f,    // every 5 meters
            locationListener
        )
    }

    fun stopTracking() {
        locationManager.removeUpdates(locationListener)
    }
}