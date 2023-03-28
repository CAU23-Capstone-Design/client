package com.lovestory.lovestory.model

import java.time.LocalDate

data class CoupleMemory(val date: LocalDate, var comment: String)

data class StringMemory(val date: String, var comment: String)

fun generateCoupleMemory(): List<CoupleMemory> = buildList {
    val currentDate = LocalDate.now()
    add(CoupleMemory(LocalDate.parse("2023-03-24"), "good"))
    add(CoupleMemory(currentDate.minusDays(10), "bad"))
    add(CoupleMemory(currentDate.minusYears(1),"What??"))
}

fun convertToStringMemory(coupleMemory: CoupleMemory): StringMemory {
    val dateString = coupleMemory.date.toString()
    return StringMemory(dateString, coupleMemory.comment)
}

fun convertToCoupleMemory(stringMemory: StringMemory): CoupleMemory {
    val date = LocalDate.parse(stringMemory.date)
    return CoupleMemory(date, stringMemory.comment)
}

fun convertToCoupleMemoryList(stringMemoryList: List<StringMemory>): List<CoupleMemory> {
    return stringMemoryList.map {
        CoupleMemory(LocalDate.parse(it.date), it.comment)
    }
}

fun convertToStringMemoryList(coupleMemoryList: List<CoupleMemory>): List<StringMemory> {
    return coupleMemoryList.map {
        StringMemory(it.date.toString(), it.comment)
    }
}

