package com.lovestory.lovestory.network

import android.graphics.Bitmap
import android.util.Log
import com.lovestory.lovestory.api.PhotoService
import com.lovestory.lovestory.model.PhotoBody
import com.lovestory.lovestory.model.PhotoTable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

suspend fun uploadPhotoToServer(
    token : String,
    imagePart: MultipartBody.Part,
    local_id : String
):Response<PhotoBody>{
    val jwt : String = "Bearer $token"
    val apiService: PhotoService = createApiService()

    return try{
        val call = apiService.uploadImage(jwt, imagePart, local_id)
        Response.success(call)
    }catch (e : HttpException){
        Log.e("NETWORK-uploadPhotoToServer", "$e")
        Response.error(e.code(), e.response()?.errorBody())
    }catch (e : Exception){
        Log.e("NETWORK-uploadPhotoToServer", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

suspend fun getPhotoTable(token : String):Response<List<String>>{
    val jwt : String = "Bearer $token"
    val apiService: PhotoService = createApiService()

//    return apiService.getImageTable(jwt)
    return try {
       apiService.getImageTable(jwt)
    } catch (e: HttpException) {
        Log.e("NETWORK-getPhotoTable", "$e")
        Response.error(e.code(), e.response()?.errorBody())
    } catch (e: Exception) {
        Log.e("NETWORK-getPhotoTable", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

suspend fun getNotSyncImage(token: String, photo_id: String):Response<ResponseBody>{
    val jwt : String = "Bearer $token"
    val apiService: PhotoService = createApiService()

    return try{
        apiService.getImage(jwt, photo_id)
    }catch (e: HttpException) {
        Log.e("NETWORK-getNotSyncImage", "$e")
        Response.error(e.code(), e.response()?.errorBody())
    } catch (e: Exception) {
        Log.e("NETWORK-getNotSyncImage", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

suspend fun getNotSyncImageInfo(token: String, photo_id: String):Response<PhotoBody>{
    val jwt : String = "Bearer $token"
    val apiService: PhotoService = createApiService()

    return try{
        apiService.getImageMetadata(jwt, photo_id)
    }catch (e: HttpException) {
        Log.e("NETWORK-getNotSyncImageInfo", "$e")
        Response.error(e.code(), e.response()?.errorBody())
    } catch (e: Exception) {
        Log.e("NETWORK-getNotSyncImageInfo", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

suspend fun getThumbnailById(token: String, photo_id : String): Response<ResponseBody> {
    val jwt : String = "Bearer $token"
    val apiService: PhotoService = createApiService()

    return try{
        apiService.getPhotoThumbnailById(jwt, photo_id)
    }catch (e: HttpException){
        Log.e("NETWORK-GetThumbnailById", "$e")
        Response.error(e.code(), e.response()?.errorBody())
    }catch (e : Exception){
        Log.e("NETWORK-GetThumbnailById", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}