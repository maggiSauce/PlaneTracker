package com.example.yourapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.em
import com.example.planetrackermobile.data.SettingsManager
import com.example.planetrackermobile.data.SettingsOptions
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.expressions.dsl.asString
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.format
import org.maplibre.compose.expressions.dsl.span
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import org.maplibre.spatialk.geojson.Position

private const val DEFAULT_MAP_URI = "https://tiles.openfreemap.org/styles/fiord"

// TODO: read docs to add planes https://maplibre.org/maplibre-native/android/examples/location-component/
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val settingsManager = SettingsManager(context)

    val selectedMapKey by settingsManager
        .getString(SettingsManager.SELECTED_THEME, SettingsOptions.themesMap.keys.first())
        .collectAsState(initial = SettingsOptions.themesMap.keys.first())
    val mapUri = SettingsOptions.themesMap[selectedMapKey] ?: DEFAULT_MAP_URI



    // mock planes
    val planes by remember {
        mutableStateOf(
            listOf(
                Triple("Flight 101", -113.50, 53.55),
                Triple("Flight 888", -113.48, 53.53)
            )
        )
    }

    val planeFeatures = planes.map { (id, lon, lat) ->
        Feature.fromGeometry(
            Point.fromLngLat(lon, lat)
        ).apply {
            addStringProperty("flightId", id)
        }
    }

    // 4. Transform plane positions into a reactive GeoJSON source


    // initialize map state
    val mapState = rememberMapState(
        baseStyle = BaseStyle.Uri(mapUri),
        initialCameraPosition = CameraPosition(
            center = Position(longitude = -113.4938, latitude = 53.5461),
            zoom = 12.0
        )
    ) {
        val planeSource = rememberGeoJsonSource(
            data = GeoJsonData.JsonString(
                FeatureCollection.fromFeatures(
                    planeFeatures
                ).toJson()
            )
        )

        SymbolLayer(
            id = "plane-layer",
            source = planeSource,
            textField = format(
                span(
                    feature["flightId"].asString()
                )
            ),
            textSize = const(12.0.em)
        )
    }

    // Display the map
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        MaplibreMap(
            state = mapState,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
