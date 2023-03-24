package com.lovestory.lovestory.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("_id")
    val id : Long,

    @SerializedName("name")
    val name : String,

    @SerializedName("birthday")
    val birthday : String,

    @SerializedName("gender")
    val gender : String,

    @SerializedName("code")
    val code : String,
)

data class UserCreated(
    val _id: String,
    val name : String,
    val birthday : String,
    val gender : String,
    val createdAt : String
)

data class UserInfo(
    val _id: Long?,
    val name : String,
    val birthday : String,
    val gender : String,
)

data class TokenUser(
    @SerializedName("_id")
    val id : Long,

    @SerializedName("name")
    val name : String,

    @SerializedName("code")
    val code : String,
)