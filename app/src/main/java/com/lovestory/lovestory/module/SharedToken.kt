package com.lovestory.lovestory.module

import android.content.Context

fun saveToken(context: Context, token: String) {
    val sharedPreferences = context.getSharedPreferences("SharedLoveStory", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString("JWT_TOKEN", token)
        apply()
    }
}

fun getToken(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("SharedLoveStory", Context.MODE_PRIVATE)
    return sharedPreferences.getString("JWT_TOKEN", null)
}