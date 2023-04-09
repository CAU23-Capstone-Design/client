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


data class PhotoTable(
//    @SerializedName("PhotoTable")
    val photoList: List<String>,
)