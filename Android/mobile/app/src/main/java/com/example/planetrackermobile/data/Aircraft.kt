package com.example.planetrackermobile.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AircraftResponse(
    val total: Int?,
    val ctime: Long?,
    val ptime: Long?,
    val ac: List<Aircraft> = emptyList()
) {
    companion object {
        fun getMockData(): AircraftResponse {
            return AircraftResponse(
                total = 2,
                ctime =  1789615524000,
                ptime =  0,
                listOf(
                    Aircraft(
                        "c03bf5",
                        "adsb_icao",
                        "WJA536  ",
                        "C-FWSI",
                        "B737",
                        "BOEING 737-700",
                        8050.0,
                        null,
                        53.375198,
                        -113.847787,
                        287.6,
                    ),
                    Aircraft(
                        "c063c0",
                        "adsb_icao",
                        "WJA536  ",
                        "CWA935",
                        "B350",
                        "BEECHCRAFT Super King Air 350",
                        19125.0,
                        null,
                        53.594009,
                        -113.67586,
                        342.5,
                    )
                )
            )
        }
    }
}

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

