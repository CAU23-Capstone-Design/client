package com.lovestory.lovestory.module

import android.util.Log
import com.lovestory.lovestory.network.getNearbyCoupleFromServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun checkNearby(token : String?):Boolean{
    Log.d("Check by", "$token")
    var result = false
    CoroutineScope(Dispatchers.Main).launch {
        val response = getNearbyCoupleFromServer(token)
        if(response.isSuccessful){
            Log.d("check nearby Location", "${response.body()}")
            result = true
        }else{
            Log.e("check nearby location error" , "${response.errorBody()}")
        }
    }
    return result
}