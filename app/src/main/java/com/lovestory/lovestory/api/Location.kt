package com.lovestory.lovestory.api

import com.lovestory.lovestory.model.LocationInfo
import com.lovestory.lovestory.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface LocationApiService {
    @Headers("Content-Type: application/json")
    @POST("/gps")
    fun sendLocation(@Header("Authorization") jwtToken: String, @Body locationData: LocationInfo): Call<Void>
}