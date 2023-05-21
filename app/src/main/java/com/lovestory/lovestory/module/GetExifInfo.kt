package com.lovestory.lovestory.module

import android.util.Log
import androidx.exifinterface.media.ExifInterface

data class ExifInfo(
    val dateTime : String,
    val latitude : Double,
    val longitude : Double
)

fun getInfoFromImage(exifInterface: ExifInterface?):ExifInfo? {
    val dateTaken = exifInterface?.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME)
    val latitude = convertDMSToDecimal(exifInterface?.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_GPS_LATITUDE))
    val longitude = convertDMSToDecimal(exifInterface?.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_GPS_LONGITUDE))

    Log.e("FUNCTION-getLocationInfoFromImage", "data : $dateTaken")
    Log.d("FUNCTION-getLocationInfoFromImage", "latitude ${latitude!!} - longitude :${longitude!!}")

//    return if(dateTaken != null && latitude != null && longitude != null){
//        ExifInfo(dateTime = dateTaken, latitude = latitude, longitude = longitude)
//    }
//    else{
//        null
//    }
    return ExifInfo(dateTime = dateTaken!!, latitude = latitude!!, longitude = longitude!!)
}

fun convertDMSToDecimal(dms: String?): Double? {
    if (dms == null) return null
    val dmsSplit = dms.split(",", limit = 3)
    val degrees = dmsSplit[0].split("/").map { it.toDouble() }.reduce { a, b -> a / b }
    val minutes = dmsSplit[1].split("/").map { it.toDouble() }.reduce { a, b -> a / b }
    val seconds = dmsSplit[2].split("/").map { it.toDouble() }.reduce { a, b -> a / b }
    return degrees + minutes / 60 + seconds / 3600
}