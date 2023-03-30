package com.lovestory.lovestory.network

import android.util.Log
import com.lovestory.lovestory.api.CalendarService
import com.lovestory.lovestory.api.CoupleService
import com.lovestory.lovestory.api.KakaoTokenService
import com.lovestory.lovestory.model.*
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate


// 목욜에 할 것들
suspend fun getComment(token : String):Response<Any>{
    val jwt : String = "Bearer $token"
    //Log.d("코루틴 토큰","$token")
    //val couple = CoupleInfo(code = code, firstDate = meetDay)
   // val couplememory = CoupleMemory(date = LocalDate.parse(date), comment = comment)
    val retrofit = Retrofit.Builder()
        .baseUrl("http://3.34.189.103:3000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(CalendarService::class.java)

    return try{
        val any : Any = apiService.getComment(jwtToken = jwt)
        Response.success(any)
    }catch (e : HttpException){
        Log.e("create couple api error", "$e")
        Response.error(e.code(), e.response()?.errorBody())
    }catch (e : Exception){
        Log.e("create couple api error2", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

/*
suspend fun sendComment(token : String, code : String?, meetDay : String?):Response<LoginResponse>{
    val jwt : String = "Bearer $token"
    val couple = CoupleInfo(code = code, firstDate = meetDay)
    val retrofit = Retrofit.Builder()
        .baseUrl("http://3.34.189.103:3000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(CoupleService::class.java)

    return try{
        val call : LoginResponse = apiService.createCouple(jwtToken = jwt, couple = couple)
//        Log.d("createCouple", "$couple")
        Response.success(call)
    }catch (e : HttpException){
        Log.e("create couple api error", "$e")
        Response.error(e.code(), e.response()?.errorBody())

    }catch (e : Exception){
        Log.e("create couple api error2", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}


 */