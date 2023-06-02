package com.lovestory.lovestory.ui.components

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lovestory.lovestory.database.entities.AdditionalPhoto
import com.lovestory.lovestory.database.entities.PhotoForSync
import com.lovestory.lovestory.database.repository.AdditionalPhotoRepository
import com.lovestory.lovestory.database.repository.PhotoForSyncRepository
import com.lovestory.lovestory.module.auth.disconnectCouple
import com.lovestory.lovestory.module.deleteToken
import com.lovestory.lovestory.module.photo.deletePhotosByIds
import com.lovestory.lovestory.network.deleteCouple
import com.lovestory.lovestory.services.LocationService
import com.lovestory.lovestory.ui.screens.getListOfNotCheckedPhoto
import com.lovestory.lovestory.ui.screens.getListOfNotCheckedPhotoFromGallery
import com.lovestory.lovestory.view.SyncedPhotoView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DeletPhotoDialog(
    showDeletePhotoDialog : MutableState<Boolean>,
    notSyncedPhotos : List<PhotoForSync>,
    additionalPhotos: List<AdditionalPhoto>,
    checkPhotoList : List<Boolean>,
    checkPhotoFromGalleryList : List<Boolean>,
    photoForSyncRepository : PhotoForSyncRepository,
    additionalPhotoRepository : AdditionalPhotoRepository,
    navHostController : NavHostController,
    context : Context
){
    AlertDialog(
        onDismissRequest = { showDeletePhotoDialog.value = false },
        title = {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)){
                Text(text="사진을 삭제하시겠습니까?",  color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        },
        text = { Text(text="사진 업로드 리스트에서 제외됩니다. 기기에 저장된 사진은 삭제되지 않습니다.",  color = Color.Black,  fontSize = 14.sp) },
        buttons = {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp), horizontalArrangement = Arrangement.SpaceEvenly
            ){
                TextButton(onClick = {
                    showDeletePhotoDialog.value = false
                    val deleteFromLoveStory =  getListOfNotCheckedPhoto(notSyncedPhotos, checkPhotoList)
                    val deleteFromGallery = getListOfNotCheckedPhotoFromGallery(additionalPhotos, checkPhotoFromGalleryList)

                    var countDeleteItem = 0
                    if(deleteFromLoveStory.isNotEmpty()){
                        for(item in deleteFromLoveStory){
                            photoForSyncRepository.deletePhotoForSync(item)
                        }
                        countDeleteItem += deleteFromLoveStory.size
                    }
                    if(deleteFromGallery.isNotEmpty()){
                        for(item in deleteFromGallery){
                            additionalPhotoRepository.deleteAdditionalPhoto(item)
                        }
                        countDeleteItem += deleteFromGallery.size
                    }

                    Toast.makeText(context, "${countDeleteItem}개의 사진을 삭제했습니다.", Toast.LENGTH_SHORT).show()
//                    if(notSyncedPhotos.size == 1){
//                        navHostController.popBackStack()
//                    }
                }) {
                    Text(text="확인",color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                TextButton(onClick = { showDeletePhotoDialog.value = false }) {
                    Text("취소", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        },
        shape = RoundedCornerShape(15.dp),
    )
}

@Composable
fun DeleteSyncPhotosDialog(
    showDeleteSyncedPhotoDialog : MutableState<Boolean>,
    isPressedPhotoMode : MutableState<Boolean>,
    countSelectedPhotos : MutableState<Int>,
    syncedPhotoView : SyncedPhotoView,
    context : Context
){
    AlertDialog(
        onDismissRequest = {  },
        title = {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)){
                Text(text="사진을 삭제하시겠습니까?",  color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        },
        text = { Text(text="${countSelectedPhotos.value}장의 사진이 Lovestory에서 삭제됩니다.",  color = Color.Black,  fontSize = 14.sp) },
        buttons = {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp), horizontalArrangement = Arrangement.SpaceEvenly
            ){
                TextButton(onClick = {
                    showDeleteSyncedPhotoDialog.value = false
                    CoroutineScope(Dispatchers.IO).launch {
                        deletePhotosByIds(
                            context,
                            syncedPhotoView,
                        )
                        isPressedPhotoMode.value = false
                        withContext(Dispatchers.Main){
                            Toast.makeText(context, "${countSelectedPhotos.value}장의 사진이 삭제 되었습니다.", Toast.LENGTH_SHORT).show()
                            countSelectedPhotos.value = 0
                        }
                    }
                }) {
                    Text(text="확인",color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                TextButton(onClick = { showDeleteSyncedPhotoDialog.value = false }) {
                    Text("취소", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        },
        shape = RoundedCornerShape(15.dp),
    )
}


const val male = 0
const val female = 1
@Composable
fun LogoutDialog(
    context : Context,
    showLogoutDialog : MutableState<Boolean>
){
    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)

    AlertDialog(
        modifier = Modifier.wrapContentHeight().width(360.dp),
        shape = RoundedCornerShape(12.dp),
        onDismissRequest = { showLogoutDialog.value = false },
        title = {
            Text(
                text = "로그아웃",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = { Text(
            text = "정말로 로그아웃 하시겠습니까?",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        ) },
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                TextButton(
                    onClick = {
                        showLogoutDialog.value = false
                        val locationServiceIntent = Intent(context, LocationService::class.java)
                        context.stopService(locationServiceIntent)
                        deleteToken(context = context)
                        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        context.startActivity(intent)
                    }
                ){
                    Text("확인", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                TextButton(
                    onClick = { showLogoutDialog.value = false}
                ){
                    Text("취소", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    )
}

@Composable
fun DisconnectDialog(
    context : Context,
    showDisconnectDialog : MutableState<Boolean>,
){
    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)

    AlertDialog(
        modifier = Modifier.wrapContentHeight().width(360.dp),
        shape = RoundedCornerShape(12.dp),
        onDismissRequest = { showDisconnectDialog.value = false },
        title = {
            Text(
                text = "상대방과 연결 끊기",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
//            Text(
//                text = "정말로 연결을 끊으시겠습니까?",
//                fontSize = 16.sp
//            )
            Column(){
                    Text(
                        text = "상대방과 연결을 끊을 경우 데이터 복구가 불가능합니다.\n정말로 연결을 끊으시겠습니까?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
               },
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                TextButton(
                    onClick = {
                        showDisconnectDialog.value = false

                        disconnectCouple(context)

                        val locationServiceIntent = Intent(context, LocationService::class.java)
                        context.stopService(locationServiceIntent)
                        deleteToken(context = context)
                        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        context.startActivity(intent)
                    }
                ){
                    Text("확인", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                TextButton(
                    onClick = { showDisconnectDialog.value = false}
                ){
                    Text("취소", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    )
}

@Composable
fun DeleteCommentDialog(
    context : Context,
    showDeleteDialog : MutableState<Boolean>,
    deleteCheck : MutableState<Boolean>
){
    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)

    AlertDialog(
        modifier = Modifier.wrapContentHeight().width(360.dp),
        shape = RoundedCornerShape(12.dp),
        onDismissRequest = { showDeleteDialog.value = false },
        title = {
            Text(
                text = "코멘트 삭제",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Text(
            text = "정말로 코멘트를 삭제 하시겠습니까?\n코멘트 외에는 삭제되지 않습니다.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            )
               },
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                TextButton(
                    onClick = {
                        showDeleteDialog.value = false
                        deleteCheck.value = true
                    }
                ){
                    Text("확인", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                TextButton(
                    onClick = {
                        showDeleteDialog.value = false
                    }
                ){
                    Text("취소", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    )
}