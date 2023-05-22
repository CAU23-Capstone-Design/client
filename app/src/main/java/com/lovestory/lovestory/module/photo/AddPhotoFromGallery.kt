package com.lovestory.lovestory.module.photo

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.entities.AdditionalPhoto
import com.lovestory.lovestory.database.repository.AdditionalPhotoRepository
import com.lovestory.lovestory.module.getInfoFromImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.security.MessageDigest

fun addPhotoFromGallery(uri : List<Uri>, context : Context){
    val photoDatabase = PhotoDatabase.getDatabase(context)
    val additionalPhotoDao = photoDatabase.additionalPhotoDao()
    val additionalPhotoRepository = AdditionalPhotoRepository(additionalPhotoDao)

    val ioScope = CoroutineScope(Dispatchers.IO)
    var countNotExistLocationInfo = 0
    var countDuplicatePhoto = 0

    uri.map { photo_url ->
        Log.d("DropDown Menu", "$photo_url")
        val id = photo_url.toString().substringAfterLast("/media/").substringBeforeLast("/")
        val transUri = "content://media/external/images/media/$id"
        val uri = Uri.parse(transUri)

        val inputStream = context.contentResolver.openInputStream(uri)
        val exifInterface = inputStream?.let { androidx.exifinterface.media.ExifInterface(it) }
        val uriItemInfo = getInfoFromImage(exifInterface = exifInterface)

        if (uriItemInfo != null) {
            Log.d("FUNCTION-getLocationInfoFromImage", "latitude ${uriItemInfo!!.latitude!!} - longitude :${uriItemInfo!!.longitude!!}")
            val photoId = getUriMD5Hash(uri = photo_url)

            val addPhotoItem = AdditionalPhoto(
                id = photoId!!,
                date = uriItemInfo.dateTime,
                imageUrl = uri.toString(),
                latitude = uriItemInfo.latitude,
                longitude = uriItemInfo.longitude
            )

            if(additionalPhotoRepository.getAdditionalPhotoById(photoId!!) == null){
                additionalPhotoRepository.insertAdditionalPhoto(addPhotoItem)
            }else{
                countDuplicatePhoto += 1
            }
        }else{
            countNotExistLocationInfo +=1
        }

    }
    if(countDuplicatePhoto > 0){
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "$countDuplicatePhoto 장의 중복된 사진은 제외 되었습니다.",Toast.LENGTH_LONG).show()
        }
    }
    if(countNotExistLocationInfo > 0){
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "$countNotExistLocationInfo 장의 위치 정보 없는 사진은 제외 되었습니다.",Toast.LENGTH_LONG).show()
        }
    }
}

private fun getUriMD5Hash(uri: Uri): String? {
    val md5Hash = MessageDigest.getInstance("MD5")

    return try {
        val uriString = uri.toString()
//            val md5Hash = DigestUtils.md5Hex(uriString)
        md5Hash.update(uriString!!.toByteArray())
        val result = BigInteger(1, md5Hash.digest()).toString(16)

        result
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}