package com.example.planetrackermobile.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/*
 * API reference: https://github.com/adsbfi/opendata
 */
private const val ADSBAPI_BASE_URL = "https://opendata.adsb.fi/api/"
private const val JETAPI_BASE_URL = "https://www.jetapi.dev/"

// configure json to ignore unknown keys so we don't crash if api returns extra data
private val json = Json {
    ignoreUnknownKeys = true
}

private val adsbRetrofit = createRetrofit(ADSBAPI_BASE_URL)
private val jetApiRetrofit = createRetrofit(JETAPI_BASE_URL)

interface ADSBApiService {
    // todo: add exception handling
    @GET("v3/lat/{lat}/lon/{lon}/dist/{dist}")
    suspend fun getAircraftInArea(
        @Path("lat") lat: Double,
        @Path("lon") lon: Double,
        @Path("dist") dist: Int,
    ): ADSBApiResponse

    @GET("v2/mil")
    suspend fun getMilitaryAircraft(): ADSBApiResponse
}

// docs at: https://www.jetapi.dev/querybuilder
interface JetApiService {
    @GET("api")
    suspend fun getAircraftPhotos(
        @Query("reg") registration: String,
        @Query("photos") numPhotos: Int = 1,
        @Query("only_jp") onlyJP: Boolean = true
    ): JetApiResponse
}

// Centralized API access
// example usage:val aircraft = PlaneTrackerApi.adsbService
//    .getAircraftInArea(53.5, -113.5, 50)
//
//val photos = PlaneTrackerApi.jetService
//    .getAircraftPhotos("C-FCYX")
object PlaneTrackerAPIService {
    val adsbService: ADSBApiService by lazy {
        adsbRetrofit.create(ADSBApiService::class.java)
    }

    val jetService: JetApiService by lazy {
        jetApiRetrofit.create(JetApiService::class.java)
    }
}

private fun createRetrofit(baseUrl: String): Retrofit {
    return Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()
}