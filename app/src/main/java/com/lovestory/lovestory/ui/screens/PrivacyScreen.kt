package com.lovestory.lovestory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.lovestory.lovestory.ui.components.PdfViewCompat
import com.lovestory.lovestory.ui.components.ScreenHeaderWithBackButton

@Composable
fun PrivacyScreen(navHostController: NavHostController){
    val context = LocalContext.current
    val token = getToken(context)
    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        PdfViewCompat()
        ScreenHeaderWithBackButton(
            navHostController = navHostController,
            headerTitle = "서비스 이용약관"
        )
    }
}