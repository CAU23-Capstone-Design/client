package com.lovestory.lovestory.model


import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

//data class RequestToken(
//    val couple_id : String,
//    val user_id : String,
//)



interface KakaoTokenService {
    @POST("/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}

data class LoginRequest(val accessToken: String)

data class LoginResponse(
    @SerializedName("success")
    val success : Boolean,

    @SerializedName("message")
    val message : String,

    @SerializedName("token")
    val token : String,
)

data class LoginPayload(
    @SerializedName("user")
    val user : UserForLoginPayload,

    @SerializedName("couple")
    val couple : CoupleForLoginPayload?,

    @SerializedName("iat")
    val iat :String,

    @SerializedName("exp")
    val exp : String,
)

data class ServiceTokenPayload(
    @SerializedName("user")
    val user : UserForServicePayload,

    @SerializedName("couple")
    val couple : CoupleForLoginPayload?,

    @SerializedName("iat")
    val iat :String,

    @SerializedName("exp")
    val exp : String,

)

data class UserForLoginPayload(
    @SerializedName("_id")
    val id : String,

    @SerializedName("name")
    val name : String,

    @SerializedName("code")
    val code : String,
)

data class CoupleForLoginPayload(
    @SerializedName("_id")
    val id : String,
)

data class UserForServicePayload(
    @SerializedName("_id")
    val id : String,
)