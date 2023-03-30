package com.lovestory.lovestory.module

import android.util.Log
import com.lovestory.lovestory.network.getNearbyCoupleFromServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun checkNearby(token : String?){
    CoroutineScope(Dispatchers.Main).launch {
        val response = getNearbyCoupleFromServer(token)
        if(response.isSuccessful){
            Log.d("check nearby Location", "${response.body()}")
        }else{
            Log.e("check nearby location error" , "${response.errorBody()}")
        }
    }
}