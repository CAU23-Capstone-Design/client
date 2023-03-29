package com.lovestory.lovestory.module

import android.util.Log
import com.lovestory.lovestory.network.sendLocationToServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun saveLocation(token : String?, latitude: Double, longitude: Double){
    CoroutineScope(Dispatchers.Main).launch{
        val response = sendLocationToServer(token, latitude, longitude)
        if(response.isSuccessful){
            Log.d("save Location", "${response.body()}")
        }else{
            Log.e("save location error" , "${response.errorBody()}")
        }
    }
}