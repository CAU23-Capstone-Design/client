package com.lovestory.lovestory.network

import android.util.Log
import com.lovestory.lovestory.api.PhotoService
import com.lovestory.lovestory.model.PhotoBody
import com.lovestory.lovestory.model.PhotoInfo
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

const val TAG_UPLOAD_PHOTO_TO_SERVER = "[NETWORK]uploadPhotoToServer"
/**이미지 업로드 요청 (POST /images)**/
suspend fun uploadPhotoToServer(
    token : String,
    imagePart: MultipartBody.Part,
    local_id : String
):Response<PhotoBody>{
    val jwt : String = "Bearer $token"
    val apiService: PhotoService = createApiService()

    return try{
        val call = apiService.uploadImage(jwt, imagePart, local_id)
        Log.e(TAG_UPLOAD_PHOTO_TO_SERVER, "${call.errorBody()}")
        call
    }catch (e : HttpException){
        Log.e(TAG_UPLOAD_PHOTO_TO_SERVER, "$e")
        Response.error(e.code(), e.response()?.errorBody())
    }catch (e : Exception){
        Log.e(TAG_UPLOAD_PHOTO_TO_SERVER, "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

const val TAG_GET_PHOTO_TABLE = "[NETWORK]getPhotoTable"
/**모든 이미지 정보 얻기 요청 (GET /images/local-ids/info)**/
suspend fun getPhotoTable(token : String):Response<List<PhotoInfo>>{
    val jwt : String = "Bearer $token"
    val apiService: PhotoService = createApiService()

    return try {
       apiService.getImageTable(jwt)
    } catch (e: HttpException) {
        Log.e(TAG_GET_PHOTO_TABLE, "$e")
        Response.error(e.code(), e.response()?.errorBody())
    } catch (e: Exception) {
        Log.e(TAG_GET_PHOTO_TABLE, "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

const val TAG_GET_NOT_SYNC_IMAGE = "[NETWORK]getNotSyncImage"
/**이미지 id로 이미지 원본 얻기 요청 (GET /images/{local_id})**/
suspend fun getNotSyncImage(token: String, photo_id: String):Response<ResponseBody>{
    val jwt : String = "Bearer $token"
    val apiService: PhotoService = createApiService()

    return try{
        apiService.getImage(jwt, photo_id)
    }catch (e: HttpException) {
        Log.e(TAG_GET_NOT_SYNC_IMAGE, "$e")
        Response.error(e.code(), e.response()?.errorBody())
    } catch (e: Exception) {
        Log.e(TAG_GET_NOT_SYNC_IMAGE, "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

const val TAG_GET_NOT_SYNC_IMAGE_INFO = "[NETWORK]getNotSyncImageInfo"
/**이미지 id로 이미지 정보 얻기 요청 (GET /images/{local_id}/info)**/
suspend fun getNotSyncImageInfo(token: String, photo_id: String):Response<PhotoBody>{
    val jwt : String = "Bearer $token"
    val apiService: PhotoService = createApiService()

    return try{
        apiService.getImageMetadata(jwt, photo_id)
    }catch (e: HttpException) {
        Log.e(TAG_GET_NOT_SYNC_IMAGE_INFO, "$e")
        Response.error(e.code(), e.response()?.errorBody())
    } catch (e: Exception) {
        Log.e(TAG_GET_NOT_SYNC_IMAGE_INFO, "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

const val TAG_GET_THUMBNAIL_BY_ID = "[NETWORK]getThumbnailById"
/**이미지 id로 이미지 썸네일 얻기 요청 (GET /images/{local_id}/thumbnail)**/
suspend fun getThumbnailById(token: String, photo_id : String): Response<ResponseBody> {
    val jwt : String = "Bearer $token"
    val apiService: PhotoService = createApiService()

    return try{
        apiService.getPhotoThumbnailById(jwt, photo_id)
    }catch (e: HttpException){
        Log.e(TAG_GET_THUMBNAIL_BY_ID, "$e")
        Response.error(e.code(), e.response()?.errorBody())
    }catch (e : Exception){
        Log.e(TAG_GET_THUMBNAIL_BY_ID, "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

const val TAG_GET_DETAIL_BY_ID = "[NETWORK]getDetailById"
/**이미지 id로 압축 이미지 얻기 요청 (GET /images/{local_id})**/
suspend fun getDetailById(token: String, photo_id : String, quality : Int): Response<ResponseBody> {
    val jwt : String = "Bearer $token"
    val apiService: PhotoService = createApiService()

    return try{
        apiService.getPhotoDetailById(jwt, photo_id, quality)
    }catch (e: HttpException){
        Log.e(TAG_GET_DETAIL_BY_ID, "$e")
        Response.error(e.code(), e.response()?.errorBody())
    }catch (e : Exception){
        Log.e(TAG_GET_DETAIL_BY_ID, "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}

const val TAG_DELETE_PHOTO_BY_ID = "[NETWORK]deletePhotoById"
/**이미지 id로 이미지 삭제 요청 (DELETE /images/{local_id})**/
suspend fun deletePhotoById(token: String, photo_id : String):Response<ResponseBody>{
    val jwt : String = "Bearer $token"
    val apiService: PhotoService = createApiService()
    Log.d(TAG_DELETE_PHOTO_BY_ID, "$photo_id will deleted")

    return try{
        val result = apiService.deletePhotoById(jwt, photo_id)
        Log.d(TAG_DELETE_PHOTO_BY_ID, "$result")
        result
    }catch (e: HttpException){
        Log.e(TAG_DELETE_PHOTO_BY_ID, "$e")
        Response.error(e.code(), e.response()?.errorBody())
    }catch (e : Exception){
        Log.e(TAG_DELETE_PHOTO_BY_ID, "$e")
        Response.error(500, ResponseBody.create(null, "Unknown error"))
    }
}