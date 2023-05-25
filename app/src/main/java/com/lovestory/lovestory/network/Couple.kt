package com.lovestory.lovestory.network

import android.util.Log
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.lovestory.lovestory.model.CoupleInfo
import com.lovestory.lovestory.api.CoupleService
import com.lovestory.lovestory.model.LoginResponse
import com.lovestory.lovestory.model.UsersOfCoupleInfo

suspend fun createCouple(token : String, code : String?, meetDay : String?):Response<LoginResponse>{
    val jwt : String = "Bearer $token"
    val couple = CoupleInfo(code = code, firstDate = meetDay)
    val apiService: CoupleService = createApiService()

    return try{
        val call : LoginResponse = apiService.createCouple(jwtToken = jwt, couple = couple)
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

suspend fun deleteCouple(token: String?):ResponseBody?{
    val jwt = "Bearer $token"
    val apiService : CoupleService = createApiService()

    return try{
        val delete : Response<ResponseBody> = apiService.deleteCouple(jwtToken = jwt)
        delete.body()
    }catch (e : HttpException){
        Log.e("NETWORK-checkCouple", "$e")
//        Response.error(e.code(), e.response()?.errorBody())
        null

    }catch (e : Exception){
        Log.e("NETWORK-checkCouple", "$e")
//        Response.error(500, ResponseBody.create(null, "Unknown error"))
        null
    }
}