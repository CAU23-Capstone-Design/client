package com.lovestory.lovestory.api

import com.lovestory.lovestory.model.*
import retrofit2.http.*

interface CalendarService{
    @Headers("Content-Type: application/json")
    @GET("/memos") // 서버 endpoint
    suspend fun getComment(@Header("Authorization") jwtToken: String) : List<GetMemory>

    @Headers("Content-Type: application/json")
    @POST("/memos")
    suspend fun postComment(@Header("Authorization") jwtToken : String, @Body sendStringMemory: SendStringMemory) : Any

    @Headers("Content-Type: application/json")
    @DELETE("/memos/date/{date}") // 서버 endpoint
    suspend fun deleteComment(@Header("Authorization") jwtToken: String, @Path("date") date: String) : Any

    @Headers("Content-Type: application/json")
    @PUT("/memos/{date}") // 서버 endpoint
    suspend fun putComment(@Header("Authorization") jwtToken: String, @Path("date") date: String, @Body requestBody: PutCommentRequest) : Any

    @Headers("Content-Type: application/json")
    @GET("/gps") // 서버 endpoint
    suspend fun getGps(@Header("Authorization") jwtToken: String, @Path("date") date : String) : Any
}