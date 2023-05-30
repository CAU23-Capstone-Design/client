package com.lovestory.lovestory.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lovestory.lovestory.graphs.ProfileStack

@Composable
fun SettingMenuList(
    navHostController: NavHostController,
    showLogoutDialog : MutableState<Boolean>,
    showDisconnectDialog : MutableState<Boolean>
){
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SettingMenuItem(
                text = "사용 가이드",
                onClick = {
                    navHostController.navigate(ProfileStack.Help.route) {
                        popUpTo(ProfileStack.Help.route)
                    }
                }
            )
            SettingMenuItem(
                text ="서비스 이용약관",
                onClick = {
                    navHostController.navigate(ProfileStack.Privacy.route) {
                        popUpTo(ProfileStack.Privacy.route)
                    }
                }
            )
            SettingMenuItem(
                text = "로그아웃",
                onClick = {showLogoutDialog.value = true}
            )
            SettingMenuItem(
                text = "상대방과 연결 끊기",
                onClick = {showDisconnectDialog.value = true}
            )
        }
    }

}

@Composable
fun SettingMenuItem(text : String, onClick : () -> Unit){
    TextButton(
        onClick = onClick,
        modifier = Modifier.padding(vertical = 5.dp)
    ){
        Text(
            text = text,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
    }
}