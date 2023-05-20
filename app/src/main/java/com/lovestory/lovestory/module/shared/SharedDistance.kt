package com.lovestory.lovestory.module.shared

import android.content.Context

fun saveDistanceInfo(context: Context, distance : Int) {
    val sharedPreferences = context.getSharedPreferences("SharedLoveStory", Context.MODE_PRIVATE)

    with(sharedPreferences.edit()) {
        putInt("DISTANCE_INFO", distance)
        apply()
    }
}

fun getDistanceInfo(context: Context): Int? {
    val sharedPreferences = context.getSharedPreferences("SharedLoveStory", Context.MODE_PRIVATE)
    val getInfoFromShared = sharedPreferences.getInt("DISTANCE_INFO", 99999999)

    return if(getInfoFromShared == 99999999) {
        null
    }
    else{
        getInfoFromShared
    }
}