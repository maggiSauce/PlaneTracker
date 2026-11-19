package com.example.planetrackermobile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JetApiResponse(
    @SerialName(value = "Reg")
    val registration: String? = null,
    @SerialName(value = "Images")
    val images: List<ImageResponse> = emptyList<ImageResponse>(),
)

@Serializable
data class ImageResponse(
    @SerialName(value = "Image")
    val image: String? = null,
    @SerialName(value = "Thumbnail")
    val thumbnail: String? = null,
    @SerialName(value = "Airline")
    val airline: String? = null,
    @SerialName(value = "Aircraft")
    val aircraft: String? = null
)
