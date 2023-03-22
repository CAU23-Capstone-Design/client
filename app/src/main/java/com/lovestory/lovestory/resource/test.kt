package com.lovestory.lovestory.resource

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lovestory.lovestory.module.kakaoLogout
import com.lovestory.lovestory.module.kakaoWithdrawal

@Composable
fun ManageKakaoUser(context : Context){
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier
            .height(50.dp)
            .width(280.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ){
        Button(onClick = { kakaoWithdrawal(context) }) {
            Text(text = "탈퇴")
        }
        Button(onClick = { kakaoLogout(context) }) {
            Text(text = "로그아웃")
        }
    }
}