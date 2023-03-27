package com.lovestory.lovestory.api

import com.lovestory.lovestory.model.LoginRequest
import com.lovestory.lovestory.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface KakaoTokenService {
    @POST("/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}