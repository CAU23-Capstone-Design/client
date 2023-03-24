package com.lovestory.lovestory.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.lovestory.lovestory.module.getToken

@Composable
fun DashBoardScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val token : String? = getToken(context)
    Log.d("link couple", "$token")
    Log.d("DashBoard-Screen", "DashBoard 스크린 호출")
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ){
        Text(text = "대시보드!!")
    }
}