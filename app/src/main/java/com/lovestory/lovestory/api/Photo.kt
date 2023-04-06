package com.lovestory.lovestory.api

import com.lovestory.lovestory.model.PhotoBody
import okhttp3.MultipartBody
import retrofit2.http.*

interface PhotoService{
//    @Headers("Content-Type: application/json")
    @Multipart
    @POST("/images")
    suspend fun uploadImage(
        @Header("Authorization") jwtToken: String,
        @Part image: MultipartBody.Part,
        @Part ("local_id") local_id : String)
    : PhotoBody
}