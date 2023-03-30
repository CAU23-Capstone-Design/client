package com.lovestory.lovestory.api

import com.lovestory.lovestory.model.LocationInfo
import com.lovestory.lovestory.model.LoginResponse
import com.lovestory.lovestory.model.NearbyResponse
import retrofit2.Call
import retrofit2.http.*

interface LocationApiService {
    @Headers("Content-Type: application/json")
    @POST("/gps")
    fun sendLocation(@Header("Authorization") jwtToken: String, @Body locationData: LocationInfo): Call<Void>

    @Headers("Content-Type: application/json")
    @GET("/gps/chekc-nearby")
    fun checkNearbyCouple(@Header("Authorization") jwtToken : String?) : NearbyResponse
}