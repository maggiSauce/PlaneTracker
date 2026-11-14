package com.example.planetrackermobile.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planetrackermobile.data.SettingsManager
import com.example.planetrackermobile.data.SettingsOptions
import com.example.planetrackermobile.ui.theme.PlaneTrackerTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val settingsManager = remember {
        SettingsManager(context)
    }

    val scope = rememberCoroutineScope()

    val darkModeEnabled by settingsManager.getBoolean(
        SettingsManager.DARK_MODE_ENABLED,
        true
    )
        .collectAsState(initial = true)

    val selectedUpdateRate by settingsManager.getString(
        SettingsManager.UPDATE_RATE,
        SettingsOptions.updateRates.first()
    )
        .collectAsState(initial = SettingsOptions.updateRates.first())


    val selectedTheme by settingsManager.getString(
        SettingsManager.SELECTED_THEME,
        SettingsOptions.themesMap.keys.first()
    )
        .collectAsState(
            initial = SettingsOptions.themesMap.keys.first()
        )
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        // Dark Mode
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,

            ) {
            Text(
                text = "Dark Mode",
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = darkModeEnabled,
                onCheckedChange = { newValue ->
                    scope.launch {
                        settingsManager.saveBoolean(SettingsManager.DARK_MODE_ENABLED, newValue)
                    }
                }
            )
        }

        // Map Theme
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Theme",
                style = MaterialTheme.typography.bodyLarge
            )
            Box {
                OutlinedButton(
                    onClick = { expanded = true }
                ) {
                    Text(
                        text = selectedTheme,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    SettingsOptions.themesMap.keys.forEach { theme ->
                        DropdownMenuItem(
                            text = { Text(theme) },
                            onClick = {
                                scope.launch {
                                    settingsManager.saveString(
                                        SettingsManager.SELECTED_THEME,
                                        theme
                                    )
                                    val selectedMapKey = settingsManager
                                        .getString(SettingsManager.SELECTED_THEME, "null")
                                        .first()
                                    Log.d("MapDebug", "Selected style: $selectedMapKey")
                                    Log.d("MapDebug", "Style URI: ${SettingsOptions.themesMap[selectedMapKey]}")
                                }
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        HorizontalDivider()

        // Update Rate
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Update Rate",
                style = MaterialTheme.typography.bodyLarge
            )
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                SettingsOptions.updateRates.forEach { SelectedRate ->
                    SegmentedButton(
                        selected = selectedUpdateRate == SelectedRate,
                        onClick = {
                            scope.launch {
                                settingsManager.saveString(
                                    SettingsManager.UPDATE_RATE,
                                    SelectedRate
                                )
                            }
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = SettingsOptions.updateRates.indexOf(SelectedRate),
                            count = SettingsOptions.updateRates.size
                        )
                    ) {
                        Text(text = SelectedRate)
                    }
                }
            }
        }

        Button(
            onClick = {
                scope.launch {
                    settingsManager.printSettings()
                }
            }
        ) {
            Text("tester")
        }
    }
}

@Preview
@Composable
fun SettingsScreenLayoutPreview() {
    PlaneTrackerTheme {
        SettingsScreen()
    }
}
