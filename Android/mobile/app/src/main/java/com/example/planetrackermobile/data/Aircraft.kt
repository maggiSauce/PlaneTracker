package com.example.planetrackermobile.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AircraftResponse(
    val total: Int?,
    val ctime: Long?,
    val ptime: Long?,
    val ac: List<Aircraft>? = emptyList()
)

@Serializable
data class Aircraft(
    val hex: String,
    val type: String,
    val flight: String,
    @SerialName(value = "r")
    val tailCode: String,
    @SerialName(value = "t")
    val model: String,
    val description: String? = null,    // safety if "description" is missing from json
    @SerialName(value = "alt_geom")
    val altitude: Double,
    val category: String? = null,
    val lat: Double,
    val lon: Double,
    val dir: Double
)
