package com.lovestory.lovestory.module

import android.content.Context

//fun saveToken(context: Context, token: String) {
//    val sharedPreferences = context.getSharedPreferences("YOUR_PREFERENCE_NAME", Context.MODE_PRIVATE)
//    val editor = sharedPreferences.edit()
//    editor.putString("JWT_TOKEN", token)
//    editor.apply()
//}

fun saveToken(context: Context, token: String) {
    val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString("JWT_TOKEN", token)
        apply()
    }
}

fun getToken(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    return sharedPreferences.getString("JWT_TOKEN", null)
}