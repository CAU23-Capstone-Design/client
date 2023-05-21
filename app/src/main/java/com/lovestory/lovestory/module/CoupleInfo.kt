package com.lovestory.lovestory.module

data class CoupleInfoResponse(
    val user1: User,
    val user2: User,
    val firstDate: String
)

data class User(
    val name: String,
    val gender: String
)
