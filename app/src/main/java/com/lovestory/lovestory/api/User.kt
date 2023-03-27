package com.lovestory.lovestory.api

import com.lovestory.lovestory.model.*
import retrofit2.http.*

interface UserService {
    @GET("/users/findByCode")
    suspend fun getCode(@Query("code") findByCode: String): User
}

