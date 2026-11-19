package com.example.yourapp

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.planetrackermobile.R
import com.example.planetrackermobile.data.model.Aircraft
import com.example.planetrackermobile.data.model.SettingsManager
import com.example.planetrackermobile.data.model.SettingsOptions
import com.example.planetrackermobile.data.model.StaticSettings
import com.example.planetrackermobile.ui.screens.map.AircraftMapViewModel
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.CameraUpdate
import org.maplibre.compose.expressions.dsl.asNumber
import org.maplibre.compose.expressions.dsl.asString
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.format
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.dsl.span
import org.maplibre.compose.expressions.dsl.textOffset
import org.maplibre.compose.expressions.value.IconRotationAlignment
import org.maplibre.compose.interaction.ClickResult
import org.maplibre.compose.interaction.MapInteractions
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.DpPadding
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import org.maplibre.spatialk.geojson.Feature.Companion.getStringProperty
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
    var mapWidth by remember { mutableIntStateOf(0) }
    var mapHeight by remember { mutableIntStateOf(0) }


    var selectedFlightId by remember { mutableStateOf<String?>(null) }
    var selectedAircraft = remember(planes, selectedFlightId) {
        selectedFlightId?.let { id ->
            planes.find { it.flight == id }
        }
    }

    var showMilitaryAircraft by remember { mutableStateOf(false) }
    var isTrackingAircraft by remember { mutableStateOf(false) }
    var isZoomedOnAircraft by remember { mutableStateOf(false) }

    LifecycleResumeEffect(Unit) {
        viewModel.startPolling()

        onPauseOrDispose {
            selectedFlightId = null;
            selectedAircraft = null
            viewModel.stopPolling()
        }
    }


    //mock planes
//    val planes = AircraftResponse.getMockData().ac

    val planeFeatures = planes.map { aircraft: Aircraft ->
        org.maplibre.geojson.Feature.fromGeometry(
            Point.fromLngLat(
                aircraft.lon,
                aircraft.lat,
            )
        ).apply {
            addStringProperty("flightId", aircraft.flight)
            addStringProperty("cleanFlightId", aircraft.flight?.trim() ?: "")
            addNumberProperty("bearing", aircraft.dir)
            addStringProperty("model", aircraft.model)
        }
    }

    val trackingFeature = selectedFlightId?.let { flightId ->
        planeFeatures.filter { feature ->
            val property = feature.getProperty("flightId")

            property != null && !property.isJsonNull && property.asString == flightId
        }
    } ?: emptyList()

    // initialize map state
    val mapState = rememberMapState(
        baseStyle = BaseStyle.Uri(mapUri),
        initialCameraPosition = CameraPosition(
            center = Position(
                longitude = StaticSettings.DEFAULT_LONGITUDE,
                latitude = StaticSettings.DEFAULT_LATITUDE
            ),
            zoom = StaticSettings.DEFAULT_CAMERA_ZOOM
        ),
    ) {
        val planeSource = rememberGeoJsonSource(
            data = GeoJsonData.JsonString(
                FeatureCollection.fromFeatures(planeFeatures).toJson()
            )
        )

        val trackingPlaneSource = rememberGeoJsonSource(
            data = GeoJsonData.JsonString(
                FeatureCollection.fromFeatures(trackingFeature).toJson()
            )
        )


        SymbolLayer(
            id = "plane-labels",
            source = planeSource,
            textField = format(
                span(feature["cleanFlightId"].asString()),
                span(" | "),
                span(feature["model"].asString())
            ),
            // remember, cannot use any font; this one is known to work
            // in the future, consider making a MapTheme class to have a fallback for custom fonts
            textFont = const(listOf("Noto Sans Regular")),
            textSize = const(1.em),
            textColor = const(Color.Black),
            textOffset = textOffset(15.dp, (-15).dp)
        )

        SymbolLayer(
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
            onClick = { features ->
                val flightId = features.firstOrNull()?.getStringProperty("flightId")
                if (flightId != null) {
                    selectedFlightId = flightId
                    isTrackingAircraft = true
                    ClickResult.Consume
                } else {
                    ClickResult.Pass
                }
            }
        )

        val trackingSize = if(isZoomedOnAircraft) {
            DpSize(75.dp, 75.dp)
        } else {
            DpSize(30.dp, 30.dp)
        }
        SymbolLayer(
            id = "tracking-plane-icon",
            source = trackingPlaneSource,
            iconImage = image(
                painterResource(R.drawable.planeicon),
                size = trackingSize
            ),
            iconAllowOverlap = const(true),
            iconIgnorePlacement = const(true),
            iconRotate = feature["bearing"].asNumber(),
            iconRotationAlignment = const(IconRotationAlignment.Map),
        )

        SymbolLayer(
            id = "tracking-plane-crosshair",
            source = trackingPlaneSource,
            iconImage = image(
                painterResource(R.drawable.selection_crosshair),
                size = trackingSize
            ),
            iconAllowOverlap = const(true),
            iconIgnorePlacement = const(true)
        )
    }

    // Observe map camera changes
    val cameraPosition = mapState.cameraPosition
    LaunchedEffect(cameraPosition) {
        snapshotFlow {
            mapState.cameraPosition
        }
            .distinctUntilChanged()
            .debounce(250L)
            .collect { cameraPosition ->
                viewModel.updateSearchLocation(
                    cameraPosition.center.latitude,
                    cameraPosition.center.longitude,
                    cameraPosition.zoom,
                    mapWidth
                )
            }
    }

    // snap to selected plane
    LaunchedEffect(selectedFlightId) {
        val aircraft = selectedAircraft ?: return@LaunchedEffect
        isZoomedOnAircraft = true

        mapState.animateCamera(
            update = CameraUpdate(
                center = Position(
                    longitude = aircraft.lon,
                    latitude = aircraft.lat
                ),
                padding = DpPadding(
                    left = 0.dp,
                    top = 200.dp,
                    right = 0.dp,
                    bottom = 0.dp
                ),
                zoom = 10.0
            )
        )
    }

    // track selected plane
    LaunchedEffect(
        selectedFlightId,
        selectedAircraft?.lon,
        selectedAircraft?.lat,
        isTrackingAircraft,
    ) {
        if (!isTrackingAircraft) return@LaunchedEffect

        val aircraft = selectedAircraft ?: return@LaunchedEffect

        mapState.animateCamera(
            update = CameraUpdate(
                center = Position(
                    longitude = aircraft.lon,
                    latitude = aircraft.lat
                ),
                padding = DpPadding(
                    left = 0.dp,
                    top = 200.dp,
                    right = 0.dp,
                    bottom = 0.dp
                ),
            )
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
                },
            interactions = MapInteractions {
                camera {
                    pan {
                        onStart {
                            isTrackingAircraft = false
                        }
                    }
                    zoom {
                        onStart {
                            isZoomedOnAircraft = false
                        }
                    }
                }

                callbacks {
                    click {
                        onUnhandled { event ->
                            event.position?.let {
                                // code that runs when the map is clicked
                                selectedFlightId = null
                                isTrackingAircraft = false
                                isZoomedOnAircraft = false
                            }
                            ClickResult.Consume
                        }
                    }
                }
            }
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "Show Military Aircraft",
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = showMilitaryAircraft,
            onCheckedChange = { newValue ->
                Log.d("MapScreen", "ShowMilitaryAircraft: $newValue")
                viewModel.toggleSearchForMilitary()
                showMilitaryAircraft = newValue
            }
        )
    }

    // Overlay for selecting a plane; this code runs and renders when selectedAircraft is not null
    selectedAircraft?.let { aircraft ->
        AircraftInfoElement(
            viewModel,
            aircraft,
            onDismiss = {
                selectedFlightId = null
                isTrackingAircraft = false
                isZoomedOnAircraft = false
            },
            onActionClick = {
                Log.d("MapScreen", "Track flight clicked: ${aircraft.flight}")
            }
        )
    }
}

@Composable
fun AircraftInfoElement(
    viewModel: AircraftMapViewModel,
    aircraft: Aircraft,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onActionClick: () -> Unit
) {
    // https://developer.android.com/develop/ui/compose/components/card
    val propertyTextStyle = MaterialTheme.typography.bodyMedium

    // 1. Trigger the load once when this aircraft's tailCode changes
    LaunchedEffect(aircraft.tailCode) {
        viewModel.fetchAircraftImageUri(aircraft.tailCode)
    }

    // 2. Observe the image URL reactively from the ViewModel
    val planeImage by viewModel.currentImage.collectAsState()

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = planeImage?.imageUrl ?: planeImage?.imageRes,
                contentDescription = "Aircraft Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Flight Number: ",
                    style = propertyTextStyle
                )
                Text(
                    text = "${aircraft.flight?.trim()}",
                    style = propertyTextStyle
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tail Code: ",
                    style = propertyTextStyle
                )
                Text(
                    text = "${aircraft.tailCode}",
                    style = propertyTextStyle
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Model: ",
                    style = propertyTextStyle
                )
                Text(
                    // description is actually the name of the aircraft model
                    text = "${aircraft.desc}",
                    style = propertyTextStyle
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Altitude: ",
                    style = propertyTextStyle
                )
                Text(
                    text = "${aircraft.altitude} FT",
                    style = propertyTextStyle
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Speed: ",
                    style = propertyTextStyle
                )
                Text(
                    text = "${aircraft.groundSpeedInKnots} KTs",
                    style = propertyTextStyle
                )
            }
        }
    }
}

