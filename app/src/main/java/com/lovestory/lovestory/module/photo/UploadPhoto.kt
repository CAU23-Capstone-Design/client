package com.lovestory.lovestory.module.photo

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.entities.*
import com.lovestory.lovestory.database.repository.AdditionalPhotoRepository
import com.lovestory.lovestory.database.repository.PhotoForSyncRepository
import com.lovestory.lovestory.database.repository.SyncedPhotoRepository
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.network.uploadPhotoToServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody

const val TAG_UPLOAD_PHOTO = "[MODULE]uploadPhoto"
/**
 * 서버에 카메라로 찍은사진들을 업로드하는 함수 (POST /images)
 *
 * @param context 앱의 context
 * @param sendPhotos 업로드할 사진들
 * @param numOfCurrentUploadedPhoto 현재 업로드된 사진의 개수
 */
suspend fun uploadPhoto(
    context : Context,
    sendPhotos : List<PhotoForSync>,
    numOfCurrentUploadedPhoto : MutableState<Int>,
){
    val photoDatabase: PhotoDatabase = PhotoDatabase.getDatabase(context)

    val photoForSyncDao : PhotoForSyncDao = photoDatabase.photoForSyncDao()
    val syncedPhotoDao : SyncedPhotoDao = photoDatabase.syncedPhotoDao()

    val photoForSyncRepository = PhotoForSyncRepository(photoForSyncDao)
    val syncedPhotoRepository = SyncedPhotoRepository(syncedPhotoDao)

    val token = getToken(context)

    sendPhotos.onEachIndexed{ index, photo ->
//        Log.d(TAG_UPLOAD_PHOTO, "Uri : ${photo.imageUrl}")
        val uri = Uri.parse(photo.imageUrl)
//        Log.d(TAG_UPLOAD_PHOTO, "Uri : $uri")

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
//            Log.d(TAG_UPLOAD_PHOTO, "사진 불러오기")
            val byteArray = inputStream.readBytes()

            val requestFile = byteArray?.let {
                RequestBody.create("image/*".toMediaTypeOrNull(), it)
            }
            val imagePart = requestFile?.let {
                MultipartBody.Part.createFormData("image", "${photo.id}.jpg", it)
            }

//            Log.d(TAG_UPLOAD_PHOTO, "사진 업로드 실행")
            val response = withContext(Dispatchers.IO) {
                uploadPhotoToServer(token!!, imagePart!!, photo.id)
            }
//            Log.e(TAG_UPLOAD_PHOTO, "${response.errorBody()}")
            if(response.isSuccessful){
//                Log.d(TAG_UPLOAD_PHOTO, "${response.body()}")

                /*DB에 동기화 된 사진 정보 추가*/
                syncedPhotoRepository.insertSyncedPhoto(
                    SyncedPhoto(
                        id = response.body()!!.local_id,
                        date = response.body()!!.date,
                        area1 = response.body()!!.location.area1,
                        area2 = response.body()!!.location.area2,
                        area3 = response.body()!!.location.area3,
                        latitude = response.body()!!.latitude,
                        longitude = response.body()!!.longitude
                    )
                )

                /*카메라에서 찍은 사진 DB에서 정보 삭제*/
                photoForSyncRepository.deletePhotoForSync(photo)
                numOfCurrentUploadedPhoto.value+=1

            }else{
                Log.e(TAG_UPLOAD_PHOTO , "${response.errorBody()}")
            }
        }
    }
}

const val TAG_UPLOAD_PHOTO_FROM_GALLERY = "[MODULE]uploadPhotoFromGallery"
/**
 * 서버에 갤러리에서 선택한 사진들을 업로드하는 함수 (POST /images)
 *
 * @param context 앱의 context
 * @param sendPhotos 업로드할 사진들
 * @param numOfCurrentUploadedPhoto 현재 업로드된 사진의 개수
 */
suspend fun uploadPhotoFromGallery(
    context: Context,
    sendPhotosFromGallery : List<AdditionalPhoto>,
    numOfCurrentUploadedPhoto : MutableState<Int>
){
    val photoDatabase: PhotoDatabase = PhotoDatabase.getDatabase(context)
    val additionalPhotoDao = photoDatabase.additionalPhotoDao()
    val syncedPhotoDao : SyncedPhotoDao = photoDatabase.syncedPhotoDao()
    val additionalPhotoRepository = AdditionalPhotoRepository(additionalPhotoDao)
    val syncedPhotoRepository = SyncedPhotoRepository(syncedPhotoDao)

    val token = getToken(context)

    sendPhotosFromGallery.onEachIndexed{ index, photo ->
        val uri = Uri.parse(photo.imageUrl)
//        Log.d(TAG_UPLOAD_PHOTO_FROM_GALLERY, "Uri : $uri")

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            Log.d(TAG_UPLOAD_PHOTO_FROM_GALLERY, "사진 불러오기")
            val byteArray = inputStream.readBytes()

            val requestFile = byteArray?.let {
                RequestBody.create("image/*".toMediaTypeOrNull(), it)
            }
            val imagePart = requestFile?.let {
                MultipartBody.Part.createFormData("image", "${photo.id}.jpg", it)
            }

//            Log.d(TAG_UPLOAD_PHOTO_FROM_GALLERY, "사진 업로드 실행")
            val response = withContext(Dispatchers.IO) {
                uploadPhotoToServer(token!!, imagePart!!, photo.id) // 서버 사진 업로드 요청
            }

//            Log.e(TAG_UPLOAD_PHOTO_FROM_GALLERY, "${response.errorBody()}")
            if(response.isSuccessful){
//                Log.d(TAG_UPLOAD_PHOTO_FROM_GALLERY, "${response.body()}")

                /*DB에 동기화 된 사진 정보 추가*/
                syncedPhotoRepository.insertSyncedPhoto(
                    SyncedPhoto(
                        id = response.body()!!.local_id,
                        date = response.body()!!.date,
                        area1 = response.body()!!.location.area1,
                        area2 = response.body()!!.location.area2,
                        area3 = response.body()!!.location.area3,
                        latitude = response.body()!!.latitude,
                        longitude = response.body()!!.longitude
                    )
                )

                /*갤러리에서 불러온 사진 DB에서 정보 삭제*/
                additionalPhotoRepository.deleteAdditionalPhoto(photo)
                numOfCurrentUploadedPhoto.value+=1

            }else{
                Log.e(TAG_UPLOAD_PHOTO_FROM_GALLERY , "${response.errorBody()}")
            }
        }
    }
}

