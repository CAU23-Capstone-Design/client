package com.lovestory.lovestory.model

import java.time.LocalDate
import com.google.gson.annotations.SerializedName
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class CoupleMemory(val date: LocalDate, var comment: String){
    constructor(dateString: String, comment: String) : this(
        LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE),
        comment
    )
}

fun dateToString(date: LocalDate): String{
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return date.format(formatter)
}

fun monthToString(yearMonth: YearMonth): String{
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
    return yearMonth.format(formatter)
}

fun intmonthToString(yearMonth: YearMonth, int: Int): String{
    val dayString = String.format("%02d", int)
    return "${monthToString(yearMonth)}-$dayString"
}

data class GetMemory(
    @SerializedName("_id") val id: String,
    @SerializedName("date") val date: String,
    @SerializedName("couple_id") val coupleId: String,
    @SerializedName("content") var comment: String,
    @SerializedName("__v") val version: Int
)

data class PutCommentRequest(val content: String)

data class StringMemory(
    @SerializedName("date") val date: String,
    @SerializedName("comment") var comment: String
)

data class SendStringMemory(
    @SerializedName("content") var comment: String,
    @SerializedName("date") val date: String
)

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

