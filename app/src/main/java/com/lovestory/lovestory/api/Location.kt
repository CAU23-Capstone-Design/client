package com.lovestory.lovestory.api

import com.lovestory.lovestory.model.LocationInfo
import com.lovestory.lovestory.model.LocationResponse
import com.lovestory.lovestory.model.LoginResponse
import com.lovestory.lovestory.model.NearbyResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface LocationApiService {
    @Headers("Content-Type: application/json")
    @POST("/gps")
    suspend fun sendLocation(@Header("Authorization") jwtToken: String, @Body locationData: LocationInfo): LocationResponse

    @Headers("Content-Type: application/json")
    @GET("/gps/check-nearby")
    suspend fun checkNearbyCouple(@Header("Authorization") jwtToken : String?) : NearbyResponse
}