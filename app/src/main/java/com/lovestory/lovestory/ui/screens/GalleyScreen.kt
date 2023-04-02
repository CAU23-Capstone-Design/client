package com.lovestory.lovestory.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lovestory.lovestory.resource.vitro

@Composable
fun GalleryScreen(navHostController: NavHostController) {
    Log.d("Gallery-Screen", "갤러리 스크린 호출")

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.background(Color.White).fillMaxSize().padding(horizontal = 20.dp, vertical = 20.dp)
    ){
        Text(text = "Gallery",
            fontSize = 30.sp,
            fontFamily = vitro,
            fontWeight = FontWeight.Normal)


    }
}