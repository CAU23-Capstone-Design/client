package com.lovestory.lovestory.ui.components

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract.Contacts.Photo
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.lovestory.lovestory.R
import com.lovestory.lovestory.database.entities.AdditionalPhoto
import com.lovestory.lovestory.database.entities.PhotoForSync
import com.lovestory.lovestory.module.photo.addPhotoFromGallery
import com.lovestory.lovestory.ui.screens.getListOfNotCheckedPhoto
import com.lovestory.lovestory.ui.screens.getListOfNotCheckedPhotoFromGallery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DropDownIcon(
    isDropMenuForRemovePhoto : MutableState<Boolean>,
    showDeletePhotoDialog : MutableState<Boolean>,
    notSyncedPhotos : List<PhotoForSync>,
    additionalPhotos: List<AdditionalPhoto>,
    checkPhotoList : List<Boolean>,
    checkPhotoFromGalleryList : List<Boolean>,
    context : Context,
    ){

    val photoLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickMultipleVisualMedia(30)){uri ->
        CoroutineScope(Dispatchers.IO).launch{
            addPhotoFromGallery(uri, context)
        }
    }


    Box() {
        Icon(
            painter = painterResource(id = R.drawable.baseline_more_vert_24),
            contentDescription = null,
            modifier = Modifier.clickable {isDropMenuForRemovePhoto.value = true},
            tint = Color.Black
        )
        DropdownMenu(
            expanded = isDropMenuForRemovePhoto.value,
            onDismissRequest = { isDropMenuForRemovePhoto.value = false },
            modifier = Modifier.wrapContentSize()
        ) {
            DropdownMenuItem(
                onClick = {
                    val deleteFromLoveStory =  getListOfNotCheckedPhoto(notSyncedPhotos, checkPhotoList)
                    val deleteFromGallery = getListOfNotCheckedPhotoFromGallery(additionalPhotos, checkPhotoFromGalleryList)
                    if(deleteFromLoveStory.isNotEmpty() || deleteFromGallery.isNotEmpty()){
                        showDeletePhotoDialog.value = true
                    }else{
                        Toast.makeText(context, "삭제할 사진이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                    isDropMenuForRemovePhoto.value = false
                }
            ) {
                Text(text = "선택 안한 사진 삭제")
            }
            DropdownMenuItem(
                onClick = {
                    photoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    isDropMenuForRemovePhoto.value = false
                }
            ) {
                Text(text = "갤러리 사진 추가 하기")
            }
        }
    }
}