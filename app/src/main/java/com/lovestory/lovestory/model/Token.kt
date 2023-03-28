package com.lovestory.lovestory.model


import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val accessToken: String
    )

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

data class UserForLoginPayload(
    @SerializedName("_id")
    val id : String,

    @SerializedName("name")
    val name : String,

    @SerializedName("birthday")
    val birthday : String,

    @SerializedName("gender")
    val gender : String,

    @SerializedName("code")
    val code : String,
)

data class CoupleForLoginPayload(
    @SerializedName("couple_id")
    val id : String,

    @SerializedName("user1_id")
    val user1_id : String,

    @SerializedName("user2_id")
    val user2_id : String,

    @SerializedName("firstDate")
    val firstDate : String,
)