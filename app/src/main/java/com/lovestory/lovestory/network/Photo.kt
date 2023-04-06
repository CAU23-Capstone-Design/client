package com.lovestory.lovestory.network

import android.util.Log
import com.lovestory.lovestory.api.LocationApiService
import com.lovestory.lovestory.api.PhotoService
import com.lovestory.lovestory.model.LocationInfo
import com.lovestory.lovestory.model.PhotoBody
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

suspend fun uploadPhotoToServer(
    token : String,
    imagePart: MultipartBody.Part,
    local_id : String
):Response<PhotoBody>{

    val jwt : String = "Bearer $token"
    val retrofit = Retrofit.Builder()
        .baseUrl("http://3.34.189.103:3000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(PhotoService::class.java)


    return try{
        val call = apiService.uploadImage(jwt, imagePart, local_id)
        Response.success(call)
    }catch (e : HttpException){
        Log.e("loaction api error", "$e")
        Response.error(e.code(), e.response()?.errorBody())
    }catch (e : Exception){
        Log.e("location api error2", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}