package com.lovestory.lovestory.network

import android.util.Log
import com.lovestory.lovestory.model.User
import com.lovestory.lovestory.api.UserService
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
suspend fun checkValidCode(code : String):Response<User>{
    val apiService: UserService = createApiService()

    return try{
        val user : User = apiService.getCode(code)
        Response.success(user)
    }catch (e : HttpException){
        Log.e("NETWORK-checkValidCode","${e.response()?.errorBody()}")
        Log.e("NETWORK-checkValidCode","${e.code()}")
        Response.error(e.code(), e.response()?.errorBody())
    }catch (e : Exception){
        Log.e("NETWORK-checkValidCode","$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}





