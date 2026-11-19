package com.example.planetrackermobile.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ADSBApiResponse(
    val total: Int?,
    val ctime: Long?,
    val ptime: Long?,
    val ac: List<Aircraft> = emptyList()
) {
    companion object {
        fun getMockData(): ADSBApiResponse {
            return ADSBApiResponse(
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
    val hex: String? = null,
    val type: String? = null,
    val flight: String? = null,
    @SerialName(value = "r")
    val tailCode: String? = null,
    @SerialName(value = "t")
    val model: String? = null,
    val desc: String? = null,    // safety if "description" is missing from json
    @SerialName(value = "alt_geom")
    val altitude: Double? = null,
    @SerialName(value = "gs")
    val groundSpeedInKnots: Double? = null,
    val category: String? = null,
    val lat: Double = 25.047670775667918,
    val lon: Double = -71.73367637651693,
    val dir: Double = 0.0
)

