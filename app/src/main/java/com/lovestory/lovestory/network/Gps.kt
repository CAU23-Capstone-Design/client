package com.lovestory.lovestory.network

import android.util.Log
import com.lovestory.lovestory.api.CalendarService
import com.lovestory.lovestory.module.ClusterData
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

suspend fun getGps(token : String, date : String): Response<ClusterData> {
    val jwt : String = "Bearer $token"
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.cau-lovestory.site:3000/api-docs/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(CalendarService::class.java)

    return try{
        val any : ClusterData = apiService.getGps(jwtToken = jwt, date)
        Response.success(any)
    }catch (e : HttpException){
        Log.e("get Gps api error1", "$e")
        Response.error(e.code(), e.response()?.errorBody())
    }catch (e : Exception){
        Log.e("get Gps api error2", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}