package com.lovestory.lovestory.module

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream

fun compressImage(context: Context, uri: Uri, quality: Int): Uri? {

    val inputStream = context.contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream)

    val compressedFile = File(context.cacheDir, "compressed_image.jpg")
    val fileOutputStream = FileOutputStream(compressedFile)

    val originalExif = inputStream?.let { ExifInterface(it) }

    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream)
    fileOutputStream.flush()
    fileOutputStream.close()

    val compressedExif = ExifInterface(compressedFile.absolutePath)
    originalExif?.let {
        for (tag in exifTags) {
            val value = originalExif.getAttribute(tag)
            if (value != null) {
                compressedExif.setAttribute(tag, value)
            }
        }
        compressedExif.saveAttributes()
    }

    return Uri.fromFile(compressedFile)
}

val exifTags = listOf(
    ExifInterface.TAG_MAKE,
    ExifInterface.TAG_MODEL,
    ExifInterface.TAG_ORIENTATION,
    ExifInterface.TAG_DATETIME,
    ExifInterface.TAG_GPS_LATITUDE,
    ExifInterface.TAG_GPS_LONGITUDE,
    ExifInterface.TAG_GPS_ALTITUDE,
    ExifInterface.TAG_GPS_TIMESTAMP,
)