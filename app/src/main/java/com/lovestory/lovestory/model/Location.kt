package com.lovestory.lovestory.model

import com.google.gson.annotations.SerializedName

data class LocationInfo(
    @SerializedName("latitude")
    val latitude : Double,

    @SerializedName("longitude")
    val longitude : Double,
)

data class NearbyResponse(
    @SerializedName("isNearby")
    val isNearby : Boolean,

    @SerializedName("distance")
    val distance : Number,
)