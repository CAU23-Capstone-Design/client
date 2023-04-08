package com.lovestory.lovestory.network

import android.util.Log
import com.lovestory.lovestory.api.LocationApiService
import com.lovestory.lovestory.model.LocationInfo
import com.lovestory.lovestory.model.LocationResponse
import com.lovestory.lovestory.model.NearbyResponse
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

suspend fun sendLocation(token : String?, latitude: Double, longitude: Double): Response<LocationResponse> {
    val jwt : String = "Bearer $token"
    val locationData = LocationInfo(latitude =  latitude, longitude = longitude)
    val apiService: LocationApiService = createApiService()

    return try{
        val call = apiService.sendLocation(jwt, locationData)
        Response.success(call)
    }catch (e : HttpException){
        Log.e("NETWORK-setLocation", "$e")
        Response.error(e.code(), e.response()?.errorBody())
    }catch (e : Exception){
        Log.e("NETWORK-setLocation", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

suspend fun getNearbyCouple(token: String?):Response<NearbyResponse> {
    val jwt: String = "Bearer $token"
    val apiService: LocationApiService = createApiService()

    return try {
        val call = apiService.checkNearbyCouple(jwt)
        Response.success(call)
    } catch (e: HttpException) {
        Log.e("NETWORK-getNearbyCouple", "$e")
        Response.error(e.code(), e.response()?.errorBody())
    } catch (e: Exception) {
        Log.e("NETWORK-getNearbyCouple", "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}
