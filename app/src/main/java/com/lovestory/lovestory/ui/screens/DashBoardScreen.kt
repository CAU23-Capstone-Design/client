package com.lovestory.lovestory.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.browser.customtabs.CustomTabsClient.getPackageName
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import com.lovestory.lovestory.resource.vitro


@Composable
fun DashBoardScreen(navHostController: NavHostController) {
    val context = LocalContext.current

    LaunchedEffect(key1 = null){

    }

    Log.d("DashBoard-Screen", "DashBoard 스크린 호출")
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ){
        Text(text = "DashBoard",
            fontSize = 30.sp,
            fontFamily = vitro,
            fontWeight = FontWeight.Normal)
        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                intent.putExtra(Settings.EXTRA_SETTINGS_EMBEDDED_DEEP_LINK_INTENT_URI, Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)

//                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                val uri = Uri.fromParts("package", context.packageName, null)
//                intent.data = uri
//                intent.putExtra(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                context.startActivity(intent)
            }
        ){
            Text(text ="설정 열기")
        }
        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)

//                val uri = Uri.fromParts("package", context.packageName, null)
//                intent.data = uri
//                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
//                intent.putExtra(Settings.EXTRA_SETTINGS_EMBEDDED_DEEP_LINK_INTENT_URI, Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)

//                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                val uri = Uri.fromParts("package", context.packageName, null)
//                intent.data = uri
//                intent.putExtra(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                context.startActivity(intent)
            }
        ){
            Text(text ="권한 얻기")
        }
    }
}