package com.example.planetrackermobile.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

/*
 * API reference: https://github.com/adsbfi/opendata
 */
private const val DEFAULT_BASE_URL = "https://opendata.adsb.fi/api/"

// configure json to ignore unknown keys so we don't crash if api returns extra data
private val json = Json {
    ignoreUnknownKeys = true
}

private val retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(DEFAULT_BASE_URL)
    .build()

interface ADSBApiService {
    // todo: add exception handling
    @GET("v3/lat/{lat}/lon/{lon}/dist/{dist}")
    suspend fun getAircraftInArea(
        @Path("lat") lat: Double,
        @Path("lon") lon: Double,
        @Path("dist") dist: Int,
    ) : AircraftResponse

    @GET("v2/mil")
    suspend fun getMilitaryAircraft() : AircraftResponse
}

object ADSBApi {
    val retrofitService : ADSBApiService by lazy {
        retrofit.create(ADSBApiService::class.java)
    }
}