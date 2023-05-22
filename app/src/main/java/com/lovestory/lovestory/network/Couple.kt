package com.lovestory.lovestory.network

import android.util.Log
import com.lovestory.lovestory.model.CoupleInfo
import com.lovestory.lovestory.api.CoupleService
import com.lovestory.lovestory.model.LoginResponse
import com.lovestory.lovestory.model.UsersOfCoupleInfo
import com.lovestory.lovestory.module.CoupleInfoResponse
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

suspend fun createCouple(token : String, code : String?, meetDay : String?):Response<LoginResponse>{
    val jwt : String = "Bearer $token"
    val couple = CoupleInfo(code = code, firstDate = meetDay)
    val apiService: CoupleService = createApiService()

    return try{
        val call : LoginResponse = apiService.createCouple(jwtToken = jwt, couple = couple)
//        Log.d("createCouple", "$couple")
        Response.success(call)
    }catch (e : HttpException){
        Log.e("NETWORK-createCouple", "$e")
        Response.error(e.code(), e.response()?.errorBody())

    }catch (e : Exception){
        Log.e("NETWORK-createCouple", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

suspend fun checkCouple(token: String?):Response<LoginResponse>{
    val jwt : String = "Bearer $token"
    val apiService: CoupleService = createApiService()

    return try{
        val couple : LoginResponse = apiService.verifiedCouple(jwtToken = jwt)
//        Log.d("verifiedCouple", "$couple")
        Response.success(couple)
    }catch (e : HttpException){
        Log.e("NETWORK-checkCouple", "$e")
        Response.error(e.code(), e.response()?.errorBody())

    }catch (e : Exception){
        Log.e("NETWORK-checkCouple", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

suspend fun getUsersInfo(token : String): UsersOfCoupleInfo? {
    val jwt : String = "Bearer $token"
    val apiService: CoupleService = createApiService()

    return try{
        val result = apiService.getCouplesInfo(jwt)
        Log.d("NETWORK-getCoupleInfo", "${result.body()}")
        result.body()
    }catch (e : HttpException){
        Log.e("NETWORK-getCoupleInfo", "${e.response()?.errorBody()}")
        null
    }catch (e : Exception){
        Log.e("NETWORK-getCoupleInfo", "$e")
        Log.e("NETWORK-getCoupleInfo", "unknown error")
        null
    }
}

suspend fun getCoupleInfo(token: String?):Response<CoupleInfoResponse>{
    val jwt : String = "Bearer $token"
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.cau-lovestory.site:3000/api-docs/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(CoupleService::class.java)

    return try{
        val info : CoupleInfoResponse = apiService.coupleInfo(jwtToken = jwt)
        Response.success(info)
    }catch (e : HttpException){
        Log.e("NETWORK-checkCouple", "$e")
        Response.error(e.code(), e.response()?.errorBody())

    }catch (e : Exception){
        Log.e("NETWORK-checkCouple", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

suspend fun deleteCouple(token: String?):Response<LoginResponse>{
    val jwt = "Bearer $token"
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.cau-lovestory.site:3000/api-docs/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(CoupleService::class.java)

    return try{
        val delete : LoginResponse = apiService.deleteCouple(jwtToken = jwt)
        Response.success(delete)
    }catch (e : HttpException){
        Log.e("NETWORK-checkCouple", "$e")
        Response.error(e.code(), e.response()?.errorBody())

    }catch (e : Exception){
        Log.e("NETWORK-checkCouple", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}