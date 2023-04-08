package com.lovestory.lovestory.network

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

suspend fun getPhotoTable(token : String):Response<PhotoTable>{
    val jwt : String = "Bearer $token"
    val apiService: PhotoService = createApiService()

    return try{
        val call = apiService.getImageTable(token)
        Response.success(call)
    }catch (e : HttpException){
        Log.e("NETWORK-getPhotoTable", "$e")
        Response.error(e.code(), e.response()?.errorBody())
    }catch (e : Exception){
        Log.e("NETWORK-getPhotoTable", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }

}