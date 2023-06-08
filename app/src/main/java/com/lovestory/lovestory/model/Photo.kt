package com.lovestory.lovestory.model
import com.google.gson.annotations.SerializedName

data class PhotoBody(
    @SerializedName("local_id")
    val local_id: String,

    @SerializedName("location")
    val location : AreaInfo,

    @SerializedName("date")
    val date : String,

    @SerializedName("longitude")
    val longitude : Double,

    @SerializedName("latitude")
    val latitude : Double,
)

data class AreaInfo(
    @SerializedName("area1")
    val area1 : String,

    @SerializedName("area2")
    val area2 : String,

    @SerializedName("area3")
    val area3 : String,
)

data class PhotoInfo(
    @SerializedName("_id")
    val id: String,

    @SerializedName("couple_id")
    val couple_id: String,

    @SerializedName("user_id")
    val user_id: String,

    @SerializedName("local_id")
    val local_id: String,

    @SerializedName("date")
    val date : String,

    @SerializedName("location")
    val location : AreaInfo,

    @SerializedName("longitude")
    val longitude : Double,

    @SerializedName("latitude")
    val latitude : Double,
)