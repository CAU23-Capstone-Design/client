package com.lovestory.lovestory.network

import android.util.Log
import com.lovestory.lovestory.model.KakaoTokenService
import com.lovestory.lovestory.model.LoginRequest
import com.lovestory.lovestory.model.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body

suspend fun sendTokenToServer(accessToken: String):Response<LoginResponse> {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://3.34.189.103:3000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(KakaoTokenService::class.java)
    return try{
        val call  = apiService.login(LoginRequest(accessToken))
        Response.success(call)
    }catch (e : HttpException){
        Log.e("login api error", "$e")
        Response.error(e.code(), e.response()?.errorBody())

    }catch (e : Exception){
        Log.e("login api error2", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

//    call.enqueue(object : Callback<Void> {
//        override fun onResponse(call: Call<Void>, response: Response<Void>) {
//            if (response.isSuccessful) {
//                // Handle successful response
//            } else {
//                // Handle error response
//            }
//        }
//
//        override fun onFailure(call: Call<Void>, t: Throwable) {
//            // Handle network error
//        }
//    })