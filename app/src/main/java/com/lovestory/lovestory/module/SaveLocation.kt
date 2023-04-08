package com.lovestory.lovestory.module

import android.util.Log
import com.lovestory.lovestory.network.sendLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun saveLocation(token : String?, latitude: Double, longitude: Double){
    CoroutineScope(Dispatchers.Main).launch{
        val response = sendLocation(token, latitude, longitude)
        if(response.isSuccessful){
            Log.d("MODULE-saveLocation", "${response.body()}")
        }else{
            Log.e("MODULE-saveLocation" , "${response.errorBody()}")
        }
    }
}