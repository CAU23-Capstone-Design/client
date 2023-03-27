package com.lovestory.lovestory.model

import com.google.gson.annotations.SerializedName

data class CoupleInfo(
    @SerializedName("code")
    val code : String?,
    @SerializedName("firstDate*")
    val firstDate : String?,
)