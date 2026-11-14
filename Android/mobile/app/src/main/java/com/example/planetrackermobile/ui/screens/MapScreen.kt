package com.example.yourapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.planetrackermobile.data.SettingsManager
import com.example.planetrackermobile.data.SettingsOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.compose.location.LocationTrackingEffect
import org.maplibre.compose.location.rememberDefaultLocationProvider
import org.maplibre.compose.location.rememberLocationState
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.camera.CameraUpdate

private const val DEFAULT_MAP_URI = "https://tiles.openfreemap.org/styles/fiord"

// TODO: read docs to add planes https://maplibre.org/maplibre-native/android/examples/location-component/
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val settingsManager = SettingsManager(context)
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = lifecycleOwner.lifecycle

    val locationProvider = rememberDefaultLocationProvider()
    val locationState = rememberLocationState(locationProvider)
    val mapState = rememberMapState()

    val selectedMapKey by settingsManager
        .getString(SettingsManager.SELECTED_THEME, SettingsOptions.themesMap.keys.first())
        .collectAsState(initial = SettingsOptions.themesMap.keys.first())

    LocationTrackingEffect(locationState = locationState) {
        mapState.animateCamera(CameraUpdate(center = currentLocation.position, zoom = 15.0))
    }

    // Create the MapView only once during this composition.
    val mapView = remember(context) {
        MapView(context).apply {
            onCreate(null)

            getMapAsync { map ->

                // Set initial camera position
                map.cameraPosition = CameraPosition.Builder()
                    .target(
                        LatLng(
                            53.5461,
                            -113.4938
                        )
                    )
                    .zoom(12.0)
                    .build()
            }
        }
    }

    LaunchedEffect(selectedMapKey, mapView) {
        val selectedMapThemeUri = SettingsOptions.themesMap[selectedMapKey] ?: DEFAULT_MAP_URI
        mapView.getMapAsync { map ->
            map.setStyle(
                Style.Builder().fromUri(selectedMapThemeUri)
            )
        }
    }

    // Forward Android lifecycle events to MapLibre
    DisposableEffect(lifecycle, mapView) {
        var started = false
        var resumed = false
        var destroyed = false

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    if (!started) {
                        mapView.onStart()
                        started = true
                    }
                }

                Lifecycle.Event.ON_RESUME -> {
                    if (!resumed) {
                        mapView.onResume()
                        resumed = true
                    }
                }

                Lifecycle.Event.ON_PAUSE -> {
                    if (resumed) {
                        mapView.onPause()
                        resumed = false
                    }
                }

                Lifecycle.Event.ON_STOP -> {
                    if (started) {
                        mapView.onStop()
                        started = false
                    }
                }

                Lifecycle.Event.ON_DESTROY -> {
                    mapView.onDestroy()
                    destroyed = true
                }

                else -> Unit
            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)

            // Clean up if the composable is removed before
            // the Activity itself is destroyed.
            if (!destroyed) {
                if (resumed) mapView.onPause()
                if (started) mapView.onStop()

                mapView.onDestroy()
            }
        }
    }

    // Display the map
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )
    }
}
