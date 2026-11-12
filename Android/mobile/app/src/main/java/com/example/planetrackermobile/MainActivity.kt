package com.example.planetrackermobile

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
                            if (showMap) {
                                MapScreen()
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