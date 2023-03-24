package com.lovestory.lovestory.model

import com.google.gson.annotations.SerializedName

data class Couple(
    @SerializedName("couple_id")
    val couple_id : String,

    @SerializedName("user1_id")
    val userA_id : String,

    @SerializedName("user2_id")
    val userB_id : String,

    @SerializedName("firstDate")
    val meetDay : String
)

data class CoupleInfo(
    @SerializedName("code")
    val code : String?,
    @SerializedName("firstDate*")
    val firstDate : String?,
)