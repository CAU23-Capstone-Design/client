package com.lovestory.lovestory.model

import com.google.gson.annotations.SerializedName

data class CoupleInfo(
    @SerializedName("code")
    val code : String?,
    @SerializedName("firstDate")
    val firstDate : String?,
)

data class UsersOfCoupleInfo(
    @SerializedName("user1")
    val user1 : UserInfo,

    @SerializedName("user2")
    val user2 : UserInfo,

    @SerializedName("firstDate")
    val firstDate: String?
)

data class UserInfo(
    @SerializedName("name")
    val name : String,

    @SerializedName("gender")
    val gender : String
)