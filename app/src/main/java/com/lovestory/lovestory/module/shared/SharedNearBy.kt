package com.lovestory.lovestory.module.shared

import android.content.Context

fun saveNearBy(context : Context, isNearBy : Boolean) {
    val sharedPreferences = context.getSharedPreferences("SharedLoveStory", Context.MODE_PRIVATE)

    with(sharedPreferences.edit()) {
        putBoolean("NEARBY_INFO", isNearBy)
        apply()
    }
}

fun getNearBy(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("SharedLoveStory", Context.MODE_PRIVATE)

    return sharedPreferences.getBoolean("NEARBY_INFO", false)
}

fun deleteNearBy(context : Context) {
    val sharedPreferences = context.getSharedPreferences("SharedLoveStory", Context.MODE_PRIVATE)

    with(sharedPreferences.edit()) {
        remove("NEARBY_INFO")
        apply()
    }
}