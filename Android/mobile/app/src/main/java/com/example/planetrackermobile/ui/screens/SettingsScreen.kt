package com.example.planetrackermobile.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.planetrackermobile.ui.theme.PlaneTrackerTheme

enum class UpdateRate {
    SLOW,
    MEDIUM,
    FAST,
}

@Composable
fun SettingsScreen() {
    var isDarkMode by remember { mutableStateOf(false)}
    var selectedUpdateRate by remember { mutableStateOf(UpdateRate.MEDIUM) }

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
                checked = isDarkMode,
                onCheckedChange = { isDarkMode = it }
            )
        }

        HorizontalDivider()

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
                UpdateRate.entries.forEachIndexed { index, rate ->
                    SegmentedButton(
                        selected = selectedUpdateRate == rate,
                        onClick = { selectedUpdateRate = rate },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = UpdateRate.entries.size
                        )
                    ) {
                        Text(text = rate.name)
                    }
                }
            }
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
