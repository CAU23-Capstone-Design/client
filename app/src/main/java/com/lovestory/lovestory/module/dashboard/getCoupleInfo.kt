package com.lovestory.lovestory.module.dashboard

import android.content.Context
import android.util.Log
import com.lovestory.lovestory.model.UsersOfCoupleInfo
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.network.getUsersInfo

suspend fun requestUsersOfCoupleInfo(context : Context) : UsersOfCoupleInfo?{
    val token = getToken(context)

    val coupleInfo = getUsersInfo(token!!)

    return if(coupleInfo != null){
        saveCoupleInfo(context, coupleInfo)
        coupleInfo
    }else{
        Log.d("MODULE-requestUsersOfCoupleInfo", "null is wrong data")
        null
    }
}