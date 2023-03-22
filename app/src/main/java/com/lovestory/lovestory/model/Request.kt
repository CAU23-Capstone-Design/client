package com.lovestory.lovestory.model

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

data class CoupleInfo(
    val userA_id : String?,
    val code : String?,
    val meetDay : String?,
)