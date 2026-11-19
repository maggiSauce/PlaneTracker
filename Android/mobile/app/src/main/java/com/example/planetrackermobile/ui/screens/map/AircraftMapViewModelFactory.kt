package com.example.planetrackermobile.ui.screens.map

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.planetrackermobile.data.repository.AircraftRepository
import com.example.planetrackermobile.data.repository.ImageRepository

class AircraftMapViewModelFactory(
    private val application: Application,
    private val aircraftRepository: AircraftRepository,
    private val imageRepository: ImageRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(
                AircraftMapViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return AircraftMapViewModel(
                application,
                aircraftRepository = aircraftRepository,
                imageRepository = imageRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}