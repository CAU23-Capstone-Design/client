package com.lovestory.lovestory.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lovestory.lovestory.R
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.ui.components.ScreenHeaderWithBackButton
import com.lovestory.lovestory.ui.components.SelectMenuButtons

@Composable
fun HelpScreen(navHostController: NavHostController){
    val (selectedButton, setSelectedButton) = remember { mutableStateOf("갤러리") }
    val items = listOf<String>(
        "갤러리", "캘린더"
    )

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = selectedButton =="갤러리",
            enter = fadeIn(),
            exit = fadeOut()
        ){
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp)){
                Image(painter = painterResource(id = R.drawable.img_helpgallery), contentDescription = null, modifier = Modifier.fillMaxSize())
            }
        }
        AnimatedVisibility(
            visible = selectedButton =="캘린더",
            enter = fadeIn(),
            exit = fadeOut()
        ){
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp)){
                Image(painter = painterResource(id = R.drawable.img_helpcalendar), contentDescription = null, modifier = Modifier.fillMaxSize())
            }
        }

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ScreenHeaderWithBackButton(
                navHostController = navHostController,
                headerTitle = "사용 가이드"
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    SelectMenuButtons(
                        items = items,
                        selectedButton = selectedButton,
                        onClick = {
                            setSelectedButton(it)
                        }
                    )
                }
            }
        }
    }
}