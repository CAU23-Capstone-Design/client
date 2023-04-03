package com.lovestory.lovestory.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.room.Entity
import java.time.LocalDate
import com.google.gson.annotations.SerializedName
import java.time.format.DateTimeFormatter


data class CoupleMemory(val date: LocalDate, var comment: String){
    constructor(dateString: String, comment: String) : this(
        LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE),
        comment
    )
}

//data class StringMemory(val date: String, var comment: String)

data class GetMemory(
    @SerializedName("_id") val id: String,
    @SerializedName("date") val date: String,
    @SerializedName("couple_id") val coupleId: String,
    @SerializedName("content") var comment: String,
    @SerializedName("__v") val version: Int
)

data class StringMemory(
    @SerializedName("date") val date: String,
    @SerializedName("comment") var comment: String
)

data class SendStringMemory(
    @SerializedName("content") var comment: String,
    @SerializedName("date") val date: String
)
//val stringMemoryList = listOf<StringMemory>()

fun generateCoupleMemory(): List<CoupleMemory> = buildList {
    val currentDate = LocalDate.now()
    add(CoupleMemory(LocalDate.parse("2023-03-24"), "good"))
    add(CoupleMemory(currentDate.minusDays(10), "bad"))
    add(CoupleMemory(currentDate.minusYears(1),"What??"))
}

//var coupleMemoryList by remember { mutableStateOf(generateCoupleMemory()) }

fun convertToStingMemoryList(coupleMemoryList: List<CoupleMemory>): List<StringMemory> {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return coupleMemoryList.map { CoupleMemory ->
        StringMemory(CoupleMemory.date.format(formatter), CoupleMemory.comment)
    }
}

fun convertToCoupleMemoryList(stringMemoryList: List<StringMemory>): List<CoupleMemory> {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return stringMemoryList.map { StringMemory ->
        CoupleMemory(LocalDate.parse(StringMemory.date, formatter), StringMemory.comment)
    }
}

fun convertToStringMemory(coupleMemory: CoupleMemory): SendStringMemory {
    return SendStringMemory(
        comment = coupleMemory.comment,
        date = coupleMemory.date.toString()
    )
}

