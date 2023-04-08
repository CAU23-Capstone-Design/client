package com.lovestory.lovestory.api

import com.lovestory.lovestory.model.PhotoBody
import com.lovestory.lovestory.model.PhotoTable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface PhotoService{
    @Multipart
    @POST("/images")
    suspend fun uploadImage(
        @Header("Authorization") jwtToken: String,
        @Part image: MultipartBody.Part,
        @Part ("local_id") local_id : String)
    : PhotoBody

    @GET("/images/local-ids")
    suspend fun getImageTable() : PhotoTable

    @GET("images/{local_id}")
    @Streaming
    suspend fun getImage(@Path("local_id") localId: String): Response<ResponseBody>
}