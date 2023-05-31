package com.lovestory.lovestory.api

import com.lovestory.lovestory.model.PhotoBody
import com.lovestory.lovestory.model.PhotoInfo
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface PhotoService{

    @Multipart
    @POST("/images") /*이미지 업로드 요청*/
    suspend fun uploadImage(
        @Header("Authorization") jwtToken: String,
        @Part image: MultipartBody.Part,
        @Part ("local_id") local_id : String)
    : Response<PhotoBody>

    @Headers("Content-Type: application/json")
    @GET("/images/local-ids/info") /*모든 이미지 정보 얻기 요청*/
    suspend fun getImageTable(@Header("Authorization") jwtToken: String) : Response<List<PhotoInfo>>

    @Headers("Content-Type: application/json")
    @GET("/images/{local_id}") /*이미지 id로 이미지 원본 얻기 요청*/
    @Streaming
    suspend fun getImage(@Header("Authorization") jwtToken: String, @Path("local_id") localId: String): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @GET("/images/{local_id}/info") /*이미지 id로 이미지 정보 얻기 요청*/
    @Streaming
    suspend fun getImageMetadata(@Header("Authorization") jwtToken: String, @Path("local_id") localId: String): Response<PhotoBody>

    @Headers("Content-Type: application/json")
    @GET("/images/{local_id}/thumbnail") /*이미지 id로 이미지 썸네일 얻기 요청*/
    @Streaming
    suspend fun getPhotoThumbnailById(@Header("Authorization") jwtToken: String, @Path("local_id") localId: String):Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @GET("/images/{local_id}") /*이미지 id로 압축 이미지 얻기 요청*/
    @Streaming
    suspend fun getPhotoDetailById(@Header("Authorization") jwtToken: String, @Path("local_id") localId: String, @Query("quality") quality: Int):Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @DELETE("/images/{local_id}") /*이미지 id로 이미지 삭제 요청*/
    suspend fun deletePhotoById(@Header("Authorization") jwtToken: String, @Path("local_id") localId: String) : Response<ResponseBody>
}