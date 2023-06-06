package com.lovestory.lovestory.module.dashboard

import android.content.Context
import android.util.Log
import com.lovestory.lovestory.model.UsersOfCoupleInfo
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.network.getUsersInfo

suspend fun requestUsersOfCoupleInfo(context : Context) : UsersOfCoupleInfo?{
    val token = getToken(context)
    Log.d("Module-requestUsersOfCoupleInfo", "token: $token")


    val coupleInfo = getUsersInfo(token!!)
    Log.d("Module-requestUsersOfCoupleInfo", "coupleInfo: $coupleInfo")

    return if(coupleInfo != null){
        Log.d("MODULE-requestUsersOfCoupleInfo", "coupleInfo is not null,")
        saveCoupleInfo(context, coupleInfo)
        coupleInfo
    }else{
        Log.d("MODULE-requestUsersOfCoupleInfo", "null is wrong data")
        null
    }
}