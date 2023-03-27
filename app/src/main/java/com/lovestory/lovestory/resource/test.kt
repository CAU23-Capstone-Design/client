package com.lovestory.lovestory.resource

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lovestory.lovestory.R
import com.lovestory.lovestory.module.kakaoLogout
import com.lovestory.lovestory.module.kakaoWithdrawal

@Composable
fun ManageKakaoUser(){
    val context = LocalContext.current
    val appKey = context.getString(R.string.app_kakao_key)
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier
            .height(50.dp)
            .width(280.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ){
        Button(onClick = { kakaoWithdrawal(appKey, context) }) {
            Text(text = "탈퇴")
        }
        Button(onClick = { kakaoLogout(appKey, context) }) {
            Text(text = "로그아웃")
        }
    }
}