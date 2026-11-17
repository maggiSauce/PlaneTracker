package com.example.planetrackermobile

import com.example.planetrackermobile.data.PlaneTrackerAPIService
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.Test
import retrofit2.HttpException

class ADSBApiTest {
    @Test
    fun testRealApiCall() = runBlocking {
        val lat = 53.49764590494395
        val lon = -113.61540474272716
        val dist = 50

        try {
            val response = PlaneTrackerAPIService.retrofitService.getAircraftInArea(lat, lon, dist)
            println("Successfully fetched ${response.ac?.size} aircraft!")
            println("Response: ${response}")
            assertNotNull(response)
        } catch (e: Exception) {
            if (e is HttpException) {
                // Extract request and response details from HTTP errors (e.g., 404, 500)
                val requestUrl = e.response()?.raw()?.request?.url
                val statusCode = e.code()
                val errorBody = e.response()?.errorBody()?.string()

                println("--- Retrofit HTTP Error ---")
                println("Request URL: $requestUrl")
                println("Status Code: $statusCode")
                println("Error Body: $errorBody")
            } else {
                // Catch serialization, network, or other non-HTTP exceptions
                println("--- General Exception ---")
                println("Message: ${e.localizedMessage}")
            }

            e.printStackTrace()
            throw e
        }
    }
}