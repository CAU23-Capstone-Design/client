package com.lovestory.lovestory.module

import android.util.Log
import com.lovestory.lovestory.model.User
import com.lovestory.lovestory.model.UserInfo
import com.lovestory.lovestory.api.CoupleService
import com.lovestory.lovestory.api.UserService
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


suspend fun verifiedUserID(id : String):Response<User>{
    val retrofit = Retrofit.Builder()
        .baseUrl("http://3.34.189.103:3000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(UserService::class.java)

    return try{
        val user : User = apiService.getUser(id = id)
        Log.d("verifiedUserID", "$user")
        Response.success(user)
    }catch (e : HttpException){
        Response.error(e.code(), e.response()?.errorBody())

    }catch (e : Exception){
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

suspend fun createUser(id : Long?, name : String, birthday : String, gender : String):Response<User>{
    val user = UserInfo(
        _id = id,
        name = name,
        birthday = birthday,
        gender = gender
    )
    val retrofit = Retrofit.Builder()
        .baseUrl("http://3.34.189.103:3000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(UserService::class.java)


    return try{
        val user : User = apiService.createUser(user)
        Log.d("createUser", "$user")
        Response.success(user)
    }catch (e : HttpException){
        Response.error(e.code(), e.response()?.errorBody())

    }catch (e : Exception){
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

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





