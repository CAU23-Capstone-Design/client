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