package com.lovestory.lovestory.model

import com.google.gson.annotations.SerializedName

data class PhotoBody(
    @SerializedName("local_id")
    val local_id: String,

    @SerializedName("location")
    val message : String,

    @SerializedName("date")
    val date : String,

    @SerializedName("longitude")
    val longitude : Double,

    @SerializedName("latitude")
    val latitude : Double,
)