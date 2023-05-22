package com.lovestory.lovestory.ui.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lovestory.lovestory.resource.vitro
import com.lovestory.lovestory.ui.components.ButtonForAuth
import com.lovestory.lovestory.ui.theme.LoveStoryTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val doubleBackToExitPressedOnce = remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        if(doubleBackToExitPressedOnce.value){
            (context as Activity).finish()
        }else{
            doubleBackToExitPressedOnce.value = true
            Toast.makeText(context, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()

            coroutineScope.launch {
                delay(2000)
                doubleBackToExitPressedOnce.value = false
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = com.lovestory.lovestory.R.drawable.lovestory_screen_logo),
            contentDescription = "Service logo",
            modifier = Modifier
                .size(250.dp),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Love Story",
            fontFamily = vitro,
            fontWeight = FontWeight.Normal,
            fontSize = 36.sp,
        )
        Spacer(modifier = Modifier.height(100.dp))
        ButtonForAuth(navHostController)
    }
}

//@Preview(showSystemUi = true)
//@Composable
//fun LoginScreenPreview() {
//    LoveStoryTheme {
//        LoginScreen(navHostController = NavHostController(LocalContext.current))
//    }
//}