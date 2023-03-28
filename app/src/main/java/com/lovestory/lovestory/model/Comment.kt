package com.lovestory.lovestory.model

import java.time.LocalDate

data class CoupleMemory(val date: LocalDate, var comment: String)

fun generateCoupleMemory(): List<CoupleMemory> = buildList {
    val currentDate = LocalDate.now()
    add(CoupleMemory(LocalDate.parse("2023-03-24"), "good"))
    add(CoupleMemory(currentDate.minusDays(10), "bad"))
    add(CoupleMemory(currentDate.minusYears(1),"What??"))
}