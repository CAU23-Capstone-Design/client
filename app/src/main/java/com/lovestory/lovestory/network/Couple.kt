package com.lovestory.lovestory.network

import android.util.Log
import com.lovestory.lovestory.model.CoupleInfo
import com.lovestory.lovestory.api.CoupleService
import com.lovestory.lovestory.model.LoginResponse
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

suspend fun createCouple(token : String, code : String?, meetDay : String?):Response<LoginResponse>{
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

suspend fun checkCouple(token: String?):Response<LoginResponse>{
    val jwt : String = "Bearer $token"
    val retrofit = Retrofit.Builder()
        .baseUrl("http://3.34.189.103:3000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(CoupleService::class.java)

    return try{
        val couple : LoginResponse = apiService.verifiedCouple(jwtToken = jwt)
//        Log.d("verifiedCouple", "$couple")
        Response.success(couple)
    }catch (e : HttpException){
        Log.e("check couple api error2", "$e")
        Response.error(e.code(), e.response()?.errorBody())

    }catch (e : Exception){
        Log.e("check couple api error2", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}