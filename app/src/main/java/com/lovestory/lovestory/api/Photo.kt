package com.lovestory.lovestory.api

import com.lovestory.lovestory.model.PhotoBody
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

    @Headers("Content-Type: application/json")
    @GET("/images/local-ids")
    suspend fun getImageTable(@Header("Authorization") jwtToken: String) : Response<List<String>>

    @Headers("Content-Type: application/json")
    @GET("/images/{local_id}")
    @Streaming
    suspend fun getImage(@Header("Authorization") jwtToken: String, @Path("local_id") localId: String): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @GET("/images/{local_id}")
    @Streaming
    suspend fun getImageMetadata(@Header("Authorization") jwtToken: String, @Path("local_id") localId: String): Response<PhotoBody>
}