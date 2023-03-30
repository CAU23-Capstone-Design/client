package com.lovestory.lovestory.api

import com.lovestory.lovestory.model.LoginResponse
import com.lovestory.lovestory.model.StringMemory
import retrofit2.http.*

interface CalendarService{
    @Headers("Content-Type: application/json")
    @GET("/memos") // 서버 endpoint
    suspend fun getComment(@Header("Authorization") jwtToken: String) : Any

    @Headers("Content-Type: application/json")
    @POST("/memos")
    suspend fun postComment(@Header("Authorization") jwtToken : String, @Body stringMemory: StringMemory) : StringMemory
}