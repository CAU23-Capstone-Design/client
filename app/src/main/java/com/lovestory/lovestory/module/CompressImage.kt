package com.lovestory.lovestory.module

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*

fun compressImage(context: Context, uri: Uri, quality: Int) : Uri?{
    val cacheId = System.currentTimeMillis().toString()
    lateinit var compressedFile : File
//    val inputStream = context.contentResolver.openInputStream(uri)

//    try {
//        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
//        inputStream?.let {
//            val exifInterface = ExifInterface(it)
//
////            val bitmap = BitmapFactory.decodeStream(inputStream)
//            val options = BitmapFactory.Options()
//            options.inJustDecodeBounds = true
////            BitmapFactory.decodeStream(inputStream, null, options)
//
////            options.inSampleSize = 1
////            options.inJustDecodeBounds = false
////            val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
//            val byteArrayOutputStream = ByteArrayOutputStream()
//            inputStream.copyTo(byteArrayOutputStream)
//            val byteArrayInputStream = ByteArrayInputStream(byteArrayOutputStream.toByteArray())
//            val bitmap = BitmapFactory.decodeStream(byteArrayInputStream)
//
//
//            Log.e("FUNCTION-getLocationInfoFromImage", "bitmap : $bitmap")
//
//            return withContext(Dispatchers.IO){
//                try {
//                    compressedFile = File(context.cacheDir, "$cacheId.jpg")
//                    Log.d("MODULE-compressImage", "compresssdExif : $compressedFile")
//
//                    val fileOutputStream = FileOutputStream(compressedFile)
//                    val compressedExif = ExifInterface(compressedFile.absolutePath)
//
//                    bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream)
//                    fileOutputStream.flush()
//                    fileOutputStream.close()
//
//                    exifInterface?.let {
//                        for (tag in exifTags) {
//                            val value = exifInterface.getAttribute(tag)
//                            Log.d("MODULE-compressImage", "tag type : $tag, value : $value")
//                            if (value != null) {
//                                compressedExif.setAttribute(tag, value)
//                            }
//                        }
//                        compressedExif.saveAttributes()
//                        val checkValue = compressedExif.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP)
//                        Log.d("MODULE-compressImage", "check-value : $checkValue")
//
//                    }
////                    Uri.fromFile(compressedFile)
//                }catch(e : IOException){
//
//                }
//                Uri.fromFile(compressedFile)
//            }
//        }
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//    return Uri.fromFile(compressedFile)


    try {
        val storage = context.cacheDir // 임시 파일 경로
        val fileName = String.format("%s.%s", System.currentTimeMillis().toString(), "jpg")
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val exifInterface = inputStream?.let { ExifInterface(it) }

        val tempFile = File(storage, fileName)
        tempFile.createNewFile()

        val fos = FileOutputStream(tempFile)

        decodeBitmapFromUri(uri, context)?.apply {
            compress(Bitmap.CompressFormat.JPEG, 75, fos)
            recycle()
        } ?: throw NullPointerException()

        fos.flush()
        fos.close()

        val compressedExif = ExifInterface(tempFile.absolutePath)

        exifInterface?.let {
            for (tag in exifTags) {
                val value = exifInterface.getAttribute(tag)
                Log.d("MODULE-compressImage", "tag type : $tag, value : $value")
                if (value != null) {
                    compressedExif.setAttribute(tag, value)
                    compressedExif.saveAttributes()
                }
            }
            compressedExif.saveAttributes()
            val checkValue = compressedExif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
            Log.d("MODULE-compressImage", "check-value : $checkValue")
        }

        return Uri.parse(tempFile.absolutePath)

    } catch (e:Exception) {
//        Log.e("ERROR"""FileUtil - ${e.message}")
    }

    return null
}

val exifTags = listOf(
    ExifInterface.TAG_DATETIME,
    ExifInterface.TAG_GPS_LATITUDE,
    ExifInterface.TAG_GPS_LONGITUDE,
)

private fun decodeBitmapFromUri(uri: Uri, context: Context): Bitmap? {
    val input = BufferedInputStream(context.contentResolver.openInputStream(uri))

    input.mark(input.available())

    var bitmap: Bitmap?

    BitmapFactory.Options().run {
        inJustDecodeBounds = true
        bitmap = BitmapFactory.decodeStream(input, null, this)

        input.reset()
        inJustDecodeBounds = false

        bitmap = BitmapFactory.decodeStream(input, null, this)?.apply {
        }
    }

    input.close()

    return bitmap

}


//
//    Log.d("MODULE-compressImage", "Uri : $uri")
//    lateinit var compressedFile : File

//    return withContext(Dispatchers.IO){
//        try{

            //     val tempImage = java.io.File.createTempFile("tempFile",".jpg",context.cacheDir)
            //    tempImage.deleteOnExit() // jvm이 종료될 때 자동 삭제된다.
            //    deleteOnExit()을 사용하면 위의 파일이 존재하는지 체크하고 삭제하는 부분을 안해도 될 듯 하다.
            //    val stream = FileOutputStream(tempImage)
            //    this@createTempFile.compress(Bitmap.CompressFormat.JPEG, 100, stream)




//
//            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream)
//            fileOutputStream.flush()
//            fileOutputStream.close()

//            val compressedExif = ExifInterface(compressedFile.absolutePath)
//
//            originalExif?.let {
//                for (tag in exifTags) {
//                    val value = originalExif.getAttribute(tag)
//                    Log.d("MODULE-compressImage", "tag type : $tag, value : $value")
//                    if (value != null) {
//                        compressedExif.setAttribute(tag, value)
//                    }
//                }
//                compressedExif.saveAttributes()
//                val checkValue = compressedExif.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP)
//                Log.d("MODULE-compressImage", "check-value : $checkValue")
//
//            }
//        }catch(e : IOException){
//
//        }
//        Uri.fromFile(compressedFile)
//    }
//    return Uri.fromFile(compressedFile)


