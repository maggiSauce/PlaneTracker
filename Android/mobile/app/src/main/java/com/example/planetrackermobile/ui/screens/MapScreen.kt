package com.example.yourapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.planetrackermobile.R
import com.example.planetrackermobile.data.Aircraft
import com.example.planetrackermobile.data.AircraftMapViewModel
import com.example.planetrackermobile.data.SettingsManager
import com.example.planetrackermobile.data.SettingsOptions
import com.example.planetrackermobile.data.StaticSettings
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.expressions.dsl.asNumber
import org.maplibre.compose.expressions.dsl.asString
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.format
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.dsl.span
import org.maplibre.compose.expressions.dsl.textOffset
import org.maplibre.compose.expressions.value.IconRotationAlignment
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
fun MapScreen(
    viewModel: AircraftMapViewModel
) {
    val context = LocalContext.current
    val settingsManager = SettingsManager(context)

    val selectedMapKey by settingsManager
        .getString(SettingsManager.SELECTED_THEME, SettingsOptions.themesMap.keys.first())
        .collectAsState(initial = SettingsOptions.themesMap.keys.first())
    val mapUri = SettingsOptions.themesMap[selectedMapKey] ?: DEFAULT_MAP_URI

    val planes by viewModel.aircraft.collectAsStateWithLifecycle()
    var cameraLongitude = StaticSettings.DEFAULT_LONGITUDE
    var cameraLatitude = StaticSettings.DEFAULT_LATITUDE
    var cameraZoom = StaticSettings.DEFAULT_CAMERA_ZOOM
    var mapWidth by remember { mutableIntStateOf(0) }
    var mapHeight by remember { mutableIntStateOf(0) }

    //mock planes
//    val planes = AircraftResponse.getMockData().ac

    val planeFeatures = planes.map { aircraft: Aircraft ->
        Feature.fromGeometry(
            Point.fromLngLat(
                aircraft.lon,
                aircraft.lat,
            )
        ).apply {
            addStringProperty("flightId", aircraft.flight)
            addNumberProperty("bearing", aircraft.dir)
        }
    }

    // initialize map state
    val mapState = rememberMapState(
        baseStyle = BaseStyle.Uri(mapUri),
        initialCameraPosition = CameraPosition(
            center = Position(
                longitude = StaticSettings.DEFAULT_LONGITUDE,
                latitude = StaticSettings.DEFAULT_LATITUDE
            ),
            zoom = StaticSettings.DEFAULT_CAMERA_ZOOM
        )
    ) {
        val planeSource = rememberGeoJsonSource(
            data = GeoJsonData.JsonString(
                FeatureCollection.fromFeatures(planeFeatures).toJson()
            )
        )

        // Draw a visible red circle for every aircraft.
        SymbolLayer(
            id = "plane-labels",
            source = planeSource,
            textField = format(
                span(feature["flightId"].asString())
            ),
            // remember, cannot use any font; this one is known to work
            // in the future, consider making a MapTheme class to have a fallback for custom fonts
            textFont = const(listOf("Noto Sans Regular")),
            textSize = const(1.em),
            textColor = const(Color.Black),
            textOffset = textOffset(15.dp, (-15).dp)
        )

        SymbolLayer (
            id = "plane-icons",
            source = planeSource,
            iconImage = image(
                painterResource(R.drawable.planeicon),
                size = DpSize(32.dp, 32.dp)
            ),
            iconAllowOverlap = const(true),
            iconIgnorePlacement = const(true),
            iconRotate = feature["bearing"].asNumber(),
            iconRotationAlignment = const(IconRotationAlignment.Map),
        )
    }

    // Observe map camera changes
    val cameraPosition = mapState.cameraPosition
    LaunchedEffect(cameraPosition) {
        cameraLatitude = cameraPosition.center.latitude
        cameraLongitude = cameraPosition.center.longitude
        cameraZoom = cameraPosition.zoom

        viewModel.updateSearchLocation(
            cameraLatitude,
            cameraLongitude,
            cameraZoom,
            mapWidth
        )
    }

    // Display the map
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        MaplibreMap(
            state = mapState,
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    mapWidth = size.width
                    mapHeight = size.height
                }
        )
    }
}
