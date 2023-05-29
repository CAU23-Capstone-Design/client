package com.lovestory.lovestory.ui.screens

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
import com.lovestory.lovestory.ui.components.DisconnectDialog
import com.lovestory.lovestory.ui.components.LogoutDialog
import com.lovestory.lovestory.ui.components.SettingMenuList

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
        LogoutDialog(
            context = context,
            showLogoutDialog = showLogoutDialog,
        )
    }
    AnimatedVisibility(visible = showDisconnectDialog.value, enter = fadeIn() + expandIn(), exit = fadeOut()) {
        DisconnectDialog(
            context = context,
            showDisconnectDialog = showDisconnectDialog,
        )
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
            .background(Color(0xBBF3F3F3))
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
fun UserProfileSection(userData : UserForLoginPayload){
    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            painter = painterResource(id = R.mipmap.ic_male_char_foreground),
            contentDescription = "profile image",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = userData.name,
            color = Color.Black,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = userData.birthday.substring(0,10),
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}