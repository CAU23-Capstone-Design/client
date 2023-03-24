package com.lovestory.lovestory.module

import android.util.Log
import com.lovestory.lovestory.model.Couple
import com.lovestory.lovestory.model.CoupleInfo
import com.lovestory.lovestory.api.CoupleService
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

suspend fun createCouple(userId : String?, code : String?, meetDay : String?):Response<Couple>{
    val couple = CoupleInfo(userA_id = userId,code = code, meetDay = meetDay)
    val retrofit = Retrofit.Builder()
        .baseUrl("http://3.34.189.103:3000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(CoupleService::class.java)

    return try{
        val couple : Couple = apiService.createCouple(couple)
        Log.d("createCouple", "$couple")
        Response.success(couple)
    }catch (e : HttpException){
        Response.error(e.code(), e.response()?.errorBody())

    }catch (e : Exception){
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

suspend fun checkCouple(id : String?):Response<Couple>{
    val id = id
    val retrofit = Retrofit.Builder()
        .baseUrl("http://3.34.189.103:3000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(CoupleService::class.java)

    return try{
        val couple : Couple = apiService.verifiedCouple(id)
        Log.d("verifiedCouple", "$couple")
        Response.success(couple)
    }catch (e : HttpException){
        Response.error(e.code(), e.response()?.errorBody())

    }catch (e : Exception){
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}