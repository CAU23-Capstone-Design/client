package com.lovestory.lovestory.network

import android.util.Log
import com.lovestory.lovestory.model.User
import com.lovestory.lovestory.api.UserService
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
suspend fun checkValidCode(code : String):Response<User>{
    val retrofit = Retrofit.Builder()
        .baseUrl("http://3.34.189.103:3000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(UserService::class.java)

    return try{
        val user : User = apiService.getCode(code)
        Log.d("checkValidCode", "$user")
        Response.success(user)
    }catch (e : HttpException){
        Log.e("checkVaildCode Failed","${e.response()?.errorBody()}")
        Log.e("checkVaildCode Failed2","${e.code()}")
        Response.error(e.code(), e.response()?.errorBody())

    }catch (e : Exception){
        Log.e("checkVaildCode Failed exception","$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}





