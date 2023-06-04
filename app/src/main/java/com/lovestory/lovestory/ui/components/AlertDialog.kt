package com.lovestory.lovestory.ui.components

import android.content.Context
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

@Composable
fun DialogWithConfirmAndCancelButton(
    showDialog : MutableState<Boolean>,
    title : String,
    text : String,
    confirmText : String,
    confirmAction : () -> Unit,
){
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)){
                Text(text=title,  color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        },
        text = { Text(text=text,  color = Color.Black,  fontSize = 14.sp) },
        buttons = {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp), horizontalArrangement = Arrangement.SpaceEvenly
            ){
                TextButton(onClick = { showDialog.value = false}) {
                    Text("취소", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                TextButton(onClick = {
                    confirmAction()
                    showDialog.value = false
                }) {
                    Text(confirmText, color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        },
        shape = RoundedCornerShape(15.dp),
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
                    }
                ){
                    Text("취소", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                TextButton(
                    onClick = {
                        showDeleteDialog.value = false
                        deleteCheck.value = true
                    }
                ){
                    Text("삭제", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

            }
        }
    )
}