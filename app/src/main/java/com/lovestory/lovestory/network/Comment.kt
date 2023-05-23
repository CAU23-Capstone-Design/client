package com.lovestory.lovestory.network

import android.util.Log
import com.lovestory.lovestory.api.CalendarService
import com.lovestory.lovestory.model.*
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

suspend fun getComment(token : String):Response<List<GetMemory>>{
    val jwt : String = "Bearer $token"
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.cau-lovestory.site:3000/api-docs/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(CalendarService::class.java)

    return try{
        val any : Any = apiService.getComment(jwtToken = jwt)
        Response.success((any as List<GetMemory>))
    }catch (e : HttpException){
        Log.e("get comment api error1", "$e")
        Response.error(e.code(), e.response()?.errorBody())
    }catch (e : Exception){
        Log.e("get comment api error2", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

suspend fun deleteComment(token : String, date: String):Response<Any>{
    val jwt : String = "Bearer $token"
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.cau-lovestory.site:3000/api-docs/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(CalendarService::class.java)

    return try{
        val any : Any = apiService.deleteComment(jwtToken = jwt, date)
        Response.success((any))
    }catch (e : HttpException){
        Log.e("delete comment api error1", "$e")
        Response.error(e.code(), e.response()?.errorBody())
    }catch (e : Exception){
        Log.e("delete comment api error2", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

suspend fun putComment(token : String, date: String, comment : String):Response<Any>{
    val jwt : String = "Bearer $token"
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.cau-lovestory.site:3000/api-docs/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(CalendarService::class.java)

    return try{
        val request = PutCommentRequest(comment)
        val any : Any = apiService.putComment(jwtToken = jwt, date, request )
        Response.success((any))
    }catch (e : HttpException){
        Log.e("put comment api error1", "$e")
        Response.error(e.code(), e.response()?.errorBody())
    }catch (e : Exception){
        Log.e("put comment api error2", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}


suspend fun getDay(token : String, date : String): Response<List<Int>> {
    val jwt : String = "Bearer $token"
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.cau-lovestory.site:3000/api-docs/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(CalendarService::class.java)

    return try{
        val any : List<Int> = apiService.getDay(jwtToken = jwt, date)
        Response.success(any)
    }catch (e : HttpException){
        Log.e("get Gps api error1", "$e")
        Response.error(e.code(), e.response()?.errorBody())
    }catch (e : Exception){
        Log.e("get Gps api error2", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}