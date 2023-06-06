package com.lovestory.lovestory.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lovestory.lovestory.R
import com.lovestory.lovestory.model.UserForLoginPayload
import com.lovestory.lovestory.module.*
import com.lovestory.lovestory.module.auth.disconnectCouple
import com.lovestory.lovestory.module.dashboard.deleteCoupleInfo
import com.lovestory.lovestory.module.shared.deleteNearBy
import com.lovestory.lovestory.services.LocationService
import com.lovestory.lovestory.ui.components.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ProfileScreen(
    navHostController: NavHostController,
    userData : UserForLoginPayload
) {
    val context = LocalContext.current

    val userName = remember { mutableStateOf("") }
    val userSex = remember { mutableStateOf(0) }

    val showLogoutDialog = remember { mutableStateOf(false) }
    val showDisconnectDialog = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = null){
        if(userData.id == "unknown"){
            val token = getToken(context)
            val userDataFromToken = token?.let { getTokenInfo(it) }
            if(userDataFromToken != null){
                userName.value = userDataFromToken.user.name
                userSex.value = if(userDataFromToken.user.gender == "male" || userDataFromToken.user.gender == "M") 0 else 1
            }
        }
    }

    AnimatedVisibility(visible = showLogoutDialog.value, enter = fadeIn() + expandIn(), exit = fadeOut()){
        DialogWithConfirmAndCancelButton(
            showDialog = showLogoutDialog,
            title = "로그아웃",
            text = "정말로 로그아웃 하시겠습니까?",
            confirmText = "로그아웃"
        ) {
            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)

            showLogoutDialog.value = false
            val locationServiceIntent = Intent(context, LocationService::class.java)
            context.stopService(locationServiceIntent)
            deleteNearBy(context = context)
            deleteCoupleInfo(context = context)
            deleteToken(context = context)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
        }
    }
    AnimatedVisibility(visible = showDisconnectDialog.value, enter = fadeIn() + expandIn(), exit = fadeOut()) {
        DialogWithConfirmAndCancelButton(
            showDialog = showDisconnectDialog,
            title = "상대방과 연결 끊기",
            text = "상대방과 연결을 끊을 경우 데이터 복구가 불가능합니다.\n정말로 연결을 끊으시겠습니까?",
            confirmText = "연결 끊기"
        ) {
            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)

            showDisconnectDialog.value = false


            disconnectCouple(context)

            val locationServiceIntent = Intent(context, LocationService::class.java)
            context.stopService(locationServiceIntent)
            deleteNearBy(context = context)
            deleteCoupleInfo(context = context)
            deleteToken(context = context)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
        }
    }

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ){
        SettingHeader()
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            UserProfileSection(userData)
            Spacer(modifier = Modifier.height(60.dp))
            SettingMenuList(
                navHostController = navHostController,
                showLogoutDialog = showLogoutDialog,
                showDisconnectDialog = showDisconnectDialog
            )
        }
    }
}

@Composable
fun SettingHeader(){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color(0xFFF3F3F3))
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 20.dp)
    ){
        Text(
            text = "설정",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun UserProfileSection(
    userData : UserForLoginPayload
){
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val parsedDate = LocalDate.parse(userData.birthday.substring(0,10), formatter)
    val nextDate = parsedDate.plusDays(1)

    val outputFormatter = DateTimeFormatter.ofPattern("M월 d일")
    val output = nextDate.format(outputFormatter)

    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        AvatarWithChar(gender = userData.gender)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = userData.name,
            color = Color.Black,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "🎂 $output",
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal
        )
    }
}