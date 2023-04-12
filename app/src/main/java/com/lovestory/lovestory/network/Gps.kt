package com.lovestory.lovestory.network

import android.util.Log
import com.lovestory.lovestory.api.CalendarService
import com.lovestory.lovestory.model.GetMemory
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

suspend fun getGps(token : String, date : String): Response<Any> {
    val jwt : String = "Bearer $token"
    //Log.d("코루틴 토큰","$token")
    //val couple = CoupleInfo(code = code, firstDate = meetDay)
    // val couplememory = CoupleMemory(date = LocalDate.parse(date), comment = comment)
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.cau-lovestory.site:3000/api-docs/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(CalendarService::class.java)

    return try{
        val any : Any = apiService.getGps(jwtToken = jwt, date)
        Response.success(any)
    }catch (e : HttpException){
        Log.e("get comment api error1", "$e")
        Response.error(e.code(), e.response()?.errorBody())
    }catch (e : Exception){
        Log.e("get comment api error2", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}