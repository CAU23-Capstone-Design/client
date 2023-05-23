package com.lovestory.lovestory.module

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

fun saveBitmapToDiskCache(context: Context, bitmap: Bitmap, cacheKey: String) {
    val cacheDir = context.cacheDir
    val file = File(cacheDir, cacheKey)

    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
}

fun loadBitmapFromDiskCache(context: Context, cacheKey: String): Bitmap? {
    val cacheDir = context.cacheDir
    val file = File(cacheDir, cacheKey)

    return if (file.exists()) {
        BitmapFactory.decodeFile(file.absolutePath)
    } else {
        null
    }
}

fun saveUserInfoToDiskCache(context: Context, name: String, gender: String, cacheKey: String) {
    val cacheDir = context.cacheDir
    val file = File(cacheDir, cacheKey)

    val data = "$name|$gender" // Concatenate the name and gender strings with a separator

    FileOutputStream(file).use { out ->
        out.write(data.toByteArray()) // Write the data to the file
    }
}

fun loadUserInfoFromDiskCache(context: Context, cacheKey: String): Pair<String, String>? {
    val cacheDir = context.cacheDir
    val file = File(cacheDir, cacheKey)

    return if (file.exists()) {
        val data = file.readText() // Read the data from the file
        val parts = data.split("|") // Split the data into name and gender using the separator

        if (parts.size == 2) {
            val name = parts[0]
            val gender = parts[1]
            Pair(name, gender) // Return the name and gender as a Pair
        } else {
            null
        }
    } else {
        null
    }
}

