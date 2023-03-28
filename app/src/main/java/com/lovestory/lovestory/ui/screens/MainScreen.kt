package com.lovestory.lovestory.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lovestory.lovestory.graphs.MainNavGraph
import com.lovestory.lovestory.services.GetDeviceLocation
import com.lovestory.lovestory.ui.components.BottomNaviagtionBar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(navHostController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = {BottomNaviagtionBar(navHostController = navHostController)},
        backgroundColor = Color.White
    ) {
        GetDeviceLocation { location ->
            Log.d("Main app screen", "Location : $location")
        }
        MainNavGraph(navHostController = navHostController)
    }
}

