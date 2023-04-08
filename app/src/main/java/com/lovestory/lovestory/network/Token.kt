package com.lovestory.lovestory.network

import android.util.Log
import com.lovestory.lovestory.api.KakaoTokenService
import com.lovestory.lovestory.model.LoginRequest
import com.lovestory.lovestory.model.LoginResponse
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

suspend fun sendTokenForLogin(accessToken: String):Response<LoginResponse> {
    val apiService: KakaoTokenService = createApiService()

    return try{
        val call  = apiService.login(LoginRequest(accessToken))
        Response.success(call)
    }catch (e : HttpException){
        Log.e("NETWORK-sendTokenForLogin", "$e")
        Response.error(e.code(), e.response()?.errorBody())

    }catch (e : Exception){
        Log.e("NETWORK-sendTokenForLogin", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}