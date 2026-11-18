package com.example.planetrackermobile.data

import android.util.Log

private const val DEFAULT_IMAGE_URI = "https://fastly.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI"


class ImageRepository(
    private val api: JetApiService
) {
    suspend fun getAircraftPhotoUri(registration: String?) : String{
        if (registration.isNullOrEmpty()) {
            return DEFAULT_IMAGE_URI
        }

        try {
            val jetApiResponse = api.getAircraftPhotos(registration)
            Log.d("AircraftViewModel", "$jetApiResponse")
            return jetApiResponse.images.firstOrNull()?.image ?: DEFAULT_IMAGE_URI
        } catch (e: Exception) {
            Log.d("AircraftViewModel", "Could not get image $e")
            return DEFAULT_IMAGE_URI
        }
    }
}