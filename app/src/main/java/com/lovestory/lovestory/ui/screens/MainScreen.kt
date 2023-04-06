package com.lovestory.lovestory.ui.screens

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.graphs.MainNavGraph
import com.lovestory.lovestory.module.getLocationPermission
import com.lovestory.lovestory.module.getMediaPermission
import com.lovestory.lovestory.module.isServiceRunning
import com.lovestory.lovestory.services.*
import com.lovestory.lovestory.ui.components.BottomNaviagtionBar
import com.lovestory.lovestory.view.PhotoViewModel
import com.lovestory.lovestory.view.PhotoViewModelFactory

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(navHostController: NavHostController = rememberNavController()) {
    val context = LocalContext.current

    getLocationPermission()
//    getMediaPermission()
//    val photoDatabase = PhotoDatabase.getDatabase(context)

    val owner = LocalViewModelStoreOwner.current
    lateinit var viewModel : PhotoViewModel

    owner?.let {
        viewModel = viewModel(
            it,
            "PhotoViewModel",
            PhotoViewModelFactory(LocalContext.current.applicationContext as Application)
        )
    }

    LaunchedEffect(key1 = null){
        val locationIntent = Intent(context, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isServiceRunning(context, LocationService::class.java)){
                context.startForegroundService(locationIntent)
            }
        } else {
            if (!isServiceRunning(context, LocationService::class.java)){
                context.startService(locationIntent)
            }
        }
    }

    Scaffold(
        bottomBar = {BottomNaviagtionBar(navHostController = navHostController)},
        backgroundColor = Color.White
    ) {
        MainNavGraph(navHostController = navHostController, viewModel = viewModel)
    }
}