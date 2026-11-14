package com.example.planetrackermobile

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planetrackermobile.data.AircraftMapViewModel
import com.example.planetrackermobile.ui.screens.SettingsScreen
import com.example.planetrackermobile.ui.theme.PlaneTrackerTheme
import com.example.yourapp.MapScreen
import org.maplibre.android.MapLibre

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapLibre.getInstance(this);     // Initialize MapLibre

        enableEdgeToEdge()

        setContent {
            PlaneTrackerTheme {
                var showMap by remember { mutableStateOf(false) }

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Button(
                            onClick = { showMap = !showMap }
                        ) {
                            Text(
                                if (showMap) "Back to Settings"
                                else "Open Map"
                            )
                        }

                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            val viewModel: AircraftMapViewModel = viewModel()
                            if (showMap) {
                                MapScreen(viewModel)
                            } else {
                                SettingsScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}