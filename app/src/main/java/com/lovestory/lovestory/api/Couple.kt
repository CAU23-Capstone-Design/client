package com.lovestory.lovestory.api

import com.lovestory.lovestory.model.Couple
import com.lovestory.lovestory.model.CoupleInfo
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface CoupleService{
    @POST("/couples")
    suspend fun createCouple(@Body couple : CoupleInfo) : Couple

    @GET("/couples/findByUserId")
    suspend fun verifiedCouple(@Query("userid") id : String?) : Couple

}