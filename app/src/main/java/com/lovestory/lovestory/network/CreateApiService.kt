package com.lovestory.lovestory.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

inline fun <reified T> createApiService(): T {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.cau-lovestory.site:3000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(T::class.java)
}