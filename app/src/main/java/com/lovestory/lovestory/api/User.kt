package com.lovestory.lovestory.api

import com.lovestory.lovestory.model.*
import retrofit2.http.*

interface UserService {
    @GET("/users/{id}")
    suspend fun getUser(@Path("id") id: String): User

//    @FormUrlEncoded
    @POST ("/users")
    suspend fun createUser(@Body user: UserInfo): User

    @GET("/users/findByCode")
    suspend fun getCode(@Query("code") findByCode: String): User
}

