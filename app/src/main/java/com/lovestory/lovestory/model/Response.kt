package com.lovestory.lovestory.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.LocalDate

//data class GetUsersIDResponse (
//    @SerializedName("result")
//    @Expose
//    val result : Boolean,
//)



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

data class Couple(
    @SerializedName("couple_id")
    val couple_id : String,

    @SerializedName("user1_id")
    val userA_id : String,

    @SerializedName("user2_id")
    val userB_id : String,

    @SerializedName("firstDate*")
    val meetDay : String
)