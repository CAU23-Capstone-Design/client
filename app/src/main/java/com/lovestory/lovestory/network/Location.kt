package com.lovestory.lovestory.network

import android.util.Log
import com.lovestory.lovestory.api.LocationApiService
import com.lovestory.lovestory.model.LocationInfo
import com.lovestory.lovestory.model.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun sendLocationToServer(token : String?, latitude: Double, longitude: Double):Response<Call<Void>> {
    val jwt : String = "Bearer $token"
    val retrofit = Retrofit.Builder()
        .baseUrl("http://3.34.189.103:3000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(LocationApiService::class.java)

    val locationData = LocationInfo(latitude =  latitude, longitude = longitude)
//    val call = apiService.sendLocation(jwt, locationData)

    try{
        val call = apiService.sendLocation(jwt, locationData)
        return Response.success(call)
    }catch (e : HttpException){
        Log.e("loaction api error", "$e")
        return Response.error(e.code(), e.response()?.errorBody())
    }catch (e : Exception){
        Log.e("location api error2", "$e")
        return Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

//    call.enqueue(object : retrofit2.Callback<Void> {
//        override fun onResponse(call: Call<Void>, response: Response<Void>) {
//            if (response.isSuccessful) {
//                Log.d("LocationService", "Location data sent successfully.")
//            } else {
//                Log.e("LocationService", "Failed to send location data. Status code: ${response.code()}")
//            }
//        }
//
//        override fun onFailure(call: Call<Void>, t: Throwable) {
//            Log.e("LocationService", "Error sending location data: ${t.message}")
//        }
//    })
