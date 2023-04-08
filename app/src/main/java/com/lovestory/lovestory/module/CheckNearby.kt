package com.lovestory.lovestory.module

import com.lovestory.lovestory.network.getNearbyCouple
import kotlinx.coroutines.*

suspend fun checkNearby(token: String?): Boolean = coroutineScope {
    val resultDeferred = async(Dispatchers.IO) {
        val response = getNearbyCouple(token)
        if (response.isSuccessful) {
            response.body()!!.isNearby
        } else {
            false
        }
    }
    resultDeferred.await()
}