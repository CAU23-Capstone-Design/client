package com.lovestory.lovestory.network

import android.util.Log
import com.lovestory.lovestory.api.LocationApiService
import com.lovestory.lovestory.model.LocationInfo
import com.lovestory.lovestory.model.LocationResponse
import com.lovestory.lovestory.model.LoginResponse
import com.lovestory.lovestory.model.NearbyResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

suspend fun sendLocationToServer(token : String?, latitude: Double, longitude: Double): Response<LocationResponse> {
    val jwt : String = "Bearer $token"
    val retrofit = Retrofit.Builder()
        .baseUrl("http://3.34.189.103:3000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(LocationApiService::class.java)

    val locationData = LocationInfo(latitude =  latitude, longitude = longitude)
//    val call = apiService.sendLocation(jwt, locationData)

    return try{
        val call = apiService.sendLocation(jwt, locationData)
        Response.success(call)
    }catch (e : HttpException){
        Log.e("loaction api error", "$e")
        Response.error(e.code(), e.response()?.errorBody())
    }catch (e : Exception){
        Log.e("location api error2", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

suspend fun getNearbyCoupleFromServer(token: String?):Response<NearbyResponse> {
    val jwt: String = "Bearer $token"
    val retrofit = Retrofit.Builder()
        .baseUrl("http://3.34.189.103:3000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(LocationApiService::class.java)

    return try {
        val call = apiService.checkNearbyCouple(jwt)
        Response.success(call)
    } catch (e: HttpException) {
        Log.e("check nearby api error", "$e")
        Response.error(e.code(), e.response()?.errorBody())
    } catch (e: Exception) {
        Log.e("check nearby api error2", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}
