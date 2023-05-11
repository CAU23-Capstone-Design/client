package com.lovestory.lovestory.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.lovestory.lovestory.R
import com.lovestory.lovestory.database.entities.PhotoForSync
import com.lovestory.lovestory.ui.screens.getListOfNotCheckedPhoto

@Composable
fun DropDownIcon(
    isDropMenuForRemovePhoto : MutableState<Boolean>,
    showDeletePhotoDialog : MutableState<Boolean>,
    notSyncedPhotos : List<PhotoForSync>,
    checkPhotoList : List<Boolean>,
    context : Context
    ){
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
                    if(deleteFromLoveStory.isNotEmpty()){
                        showDeletePhotoDialog.value = true
                    }else{
                        Toast.makeText(context, "삭제할 사진이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                    isDropMenuForRemovePhoto.value = false
                }
            ) {
                Text(text = "선택 안한 사진 삭제")
            }
        }
    }
}