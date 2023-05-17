package com.lovestory.lovestory.module.dashboard

import android.content.Context
import com.google.gson.GsonBuilder
import com.lovestory.lovestory.model.UsersOfCoupleInfo

fun saveCoupleInfo(context: Context, usersOfCoupleInfo: UsersOfCoupleInfo) {
    val sharedPreferences = context.getSharedPreferences("SharedLoveStory", Context.MODE_PRIVATE)

    val gson = GsonBuilder().create()
    val strCoupleInfo = gson.toJson(usersOfCoupleInfo)

    with(sharedPreferences.edit()) {
        putString("COUPLE_INFO", strCoupleInfo)
        apply()
    }
}

fun getCoupleInfo(context: Context): UsersOfCoupleInfo? {
    val sharedPreferences = context.getSharedPreferences("SharedLoveStory", Context.MODE_PRIVATE)
    val getInfoFromShared = sharedPreferences.getString("COUPLE_INFO", null)

    return if(getInfoFromShared == null) {
        null
    }
    else{
        val gson = GsonBuilder().create()
        gson.fromJson<UsersOfCoupleInfo>(getInfoFromShared, UsersOfCoupleInfo::class.java)
    }
}