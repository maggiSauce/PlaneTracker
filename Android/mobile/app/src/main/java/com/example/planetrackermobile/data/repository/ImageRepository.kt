package com.example.planetrackermobile.data.repository

import android.util.Log
import com.example.planetrackermobile.R
import com.example.planetrackermobile.data.model.AirplaneImage
import com.example.planetrackermobile.data.remote.JetApiService

private const val DEFAULT_IMAGE_URI =
    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTMgFa-M_sps__QZHPIv7V8nntlrQmr7q33YqoB3kPI74FiW6ENQIOgSes&s=10"


class ImageRepository(
    private val api: JetApiService
) {
    suspend fun getAircraftPhoto(registration: String?): AirplaneImage {
        if (!registration.isNullOrEmpty()) {
            Log.d("ImageRepository", "Fetching image uri for tail code: $registration")
            try {
                val jetApiResponse = api.getAircraftPhotos(registration)
                val image = jetApiResponse.images.firstOrNull()
                Log.d("ImageRepository", "$jetApiResponse")

                if (image != null) {
                    return AirplaneImage(image)
                }
            } catch (e: Exception) {
                Log.d("ImageRepository", "Could not get image $e")
            }
        }

        return AirplaneImage(getRandomUri())
    }
}

private fun getRandomUri(): Int {
    val defaultImages = listOf(
        R.drawable.f22cat,
        R.drawable.soyouwanttolearntofly,
        R.drawable.sudrink,
    )
    return defaultImages.random()
}