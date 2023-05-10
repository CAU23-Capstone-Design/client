package com.lovestory.lovestory.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.lovestory.lovestory.database.entities.PhotoForSync
import com.lovestory.lovestory.database.repository.PhotoForSyncRepository
import com.lovestory.lovestory.ui.screens.getListOfNotCheckedPhoto

@Composable
fun DeletPhotoDialog(
    showDeletePhotoDialog : MutableState<Boolean>,
    notSyncedPhotos : List<PhotoForSync>,
    checkPhotoList : MutableState<MutableList<Boolean>>,
    photoForSyncRepository : PhotoForSyncRepository,
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
                    for(item in deleteFromLoveStory){
                        photoForSyncRepository.deletePhotoForSync(item)
                    }
                    Toast.makeText(context, "${deleteFromLoveStory.size}개의 사진을 삭제했습니다.", Toast.LENGTH_SHORT).show()
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