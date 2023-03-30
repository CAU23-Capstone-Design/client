package com.lovestory.lovestory.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.room.Entity
import java.time.LocalDate
import com.google.gson.annotations.SerializedName


data class CoupleMemory(val date: LocalDate, var comment: String)

//data class StringMemory(val date: String, var comment: String)

data class StringMemory(
    @SerializedName("date") val date: String,
    @SerializedName("comment") var comment: String
)

val stringMemoryList = listOf<StringMemory>()

fun generateCoupleMemory(): List<CoupleMemory> = buildList {
    val currentDate = LocalDate.now()
    add(CoupleMemory(LocalDate.parse("2023-03-24"), "good"))
    add(CoupleMemory(currentDate.minusDays(10), "bad"))
    add(CoupleMemory(currentDate.minusYears(1),"What??"))
}

//var coupleMemoryList by remember { mutableStateOf(generateCoupleMemory()) }

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

