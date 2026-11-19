package com.example.planetrackermobile.data.repository

import android.util.Log
import com.example.planetrackermobile.R
import com.example.planetrackermobile.data.remote.JetApiService

private const val DEFAULT_IMAGE_URI = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTMgFa-M_sps__QZHPIv7V8nntlrQmr7q33YqoB3kPI74FiW6ENQIOgSes&s=10"


class ImageRepository(
    private val api: JetApiService
) {
    suspend fun getAircraftPhotoUri(registration: String?) : String {
        if (registration.isNullOrEmpty()) {
            return DEFAULT_IMAGE_URI
        }

        try {
            Log.d("ImageRepository", "Fetching image uri for tail code: $registration")
            val jetApiResponse = api.getAircraftPhotos(registration)
            Log.d("ImageRepository", "$jetApiResponse")
            return jetApiResponse.images.firstOrNull()?.image ?: DEFAULT_IMAGE_URI
        } catch (e: Exception) {
            Log.d("ImageRepository", "Could not get image $e")
            return DEFAULT_IMAGE_URI
        }
    }
}

private fun getRandomUrI(): Int {
    val defaultImages = listOf(
        R.drawable.f22cat,
        R.drawable.soyouwanttolearntofly,
        R.drawable.sudrink,
    )
    return defaultImages.random()
}