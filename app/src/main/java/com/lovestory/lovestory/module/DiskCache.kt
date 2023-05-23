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

