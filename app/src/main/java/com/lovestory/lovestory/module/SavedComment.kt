package com.lovestory.lovestory.module

import android.content.Context
import com.google.gson.Gson
import com.lovestory.lovestory.model.CoupleMemory
import com.lovestory.lovestory.model.StringMemory
import com.lovestory.lovestory.model.convertToCoupleMemoryList

fun saveComment(context: Context, coupleMemoryList: List<CoupleMemory>){
    val gson = Gson()
    val stringMemoryList = coupleMemoryList.map { StringMemory(it.date.toString(), it.comment) }
    val coupleMemoryListJson = gson.toJson(stringMemoryList)

    val sharedPref = context.getSharedPreferences("comment", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString("coupleMemoryList", coupleMemoryListJson)
    editor.apply()
}

fun getSavedComment(context: Context): List<CoupleMemory> {
    val sharedPref = context.getSharedPreferences("comment", Context.MODE_PRIVATE)
    val coupleMemoryListJson = sharedPref.getString("coupleMemoryList", null)

    if (coupleMemoryListJson != null) {
        val gson = Gson()
        val stringMemoryList = gson.fromJson(coupleMemoryListJson, Array<StringMemory>::class.java).toList()
        return convertToCoupleMemoryList(stringMemoryList)
    } else {
        return emptyList() // Return an empty list if no data is found
    }
}

