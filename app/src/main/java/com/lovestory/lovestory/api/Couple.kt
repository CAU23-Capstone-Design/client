package com.lovestory.lovestory.api

import com.lovestory.lovestory.model.Couple
import com.lovestory.lovestory.model.CoupleInfo
import com.lovestory.lovestory.model.LoginResponse
import retrofit2.http.*


interface CoupleService{
    @Headers("Content-Type: application/json")
    @POST("/couples")
    suspend fun createCouple(@Header("Authorization") jwtToken: String, @Body couple : CoupleInfo) : LoginResponse

    @Headers("Content-Type: application/json")
    @GET("/couples")
    suspend fun verifiedCouple(@Header("Authorization") jwtToken : String) : LoginResponse

}