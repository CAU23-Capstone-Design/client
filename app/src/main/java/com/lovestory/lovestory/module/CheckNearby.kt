package com.lovestory.lovestory.module

import com.lovestory.lovestory.model.NearbyResponse
import com.lovestory.lovestory.network.getNearbyCouple
import kotlinx.coroutines.*

suspend fun checkNearby(token: String?): NearbyResponse? = coroutineScope {
    val resultDeferred = async(Dispatchers.IO) {
        val response = getNearbyCouple(token)
        if (response.isSuccessful) {
            val result : NearbyResponse = response.body()!!
            result
        } else {
            null
        }
    }
    resultDeferred.await()
}