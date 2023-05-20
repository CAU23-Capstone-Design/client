package com.lovestory.lovestory.ui.screens

import android.annotation.SuppressLint
import android.app.Application
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
import com.lovestory.lovestory.database.repository.AdditionalPhotoRepository
import com.lovestory.lovestory.graphs.MainNavGraph
import com.lovestory.lovestory.module.getLocationPermission
import com.lovestory.lovestory.services.*
import com.lovestory.lovestory.ui.components.BottomNaviagtionBar
import com.lovestory.lovestory.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(navHostController: NavHostController = rememberNavController()) {
    val context = LocalContext.current

//    getLocationPermission()
//    getMediaPermission()
//    val photoDatabase = PhotoDatabase.getDatabase(context)

    val owner = LocalViewModelStoreOwner.current
    lateinit var photoForSyncView: PhotoForSyncView
    lateinit var syncedPhotoView: SyncedPhotoView

    lateinit var imageSyncView: ImageSyncView

    owner?.let {
        photoForSyncView = viewModel(
            it,
            "PhotoForSyncViewModel",
            PhotoForSyncViewFactory(LocalContext.current.applicationContext as Application)
        )

        syncedPhotoView = viewModel(
            it,
            "SyncedPhotoViewModel",
            SyncedPhotoViewFactory(LocalContext.current.applicationContext as Application)
        )

        imageSyncView= viewModel(
            it,
            "ImageSyncViewModel",
            ImageSyncViewFactory()
        )
    }



    LaunchedEffect(key1 = null){
//        val locationIntent = Intent(context, LocationService::class.java)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            if (!isServiceRunning(context, LocationService::class.java)){
//                context.startForegroundService(locationIntent)
//            }
//        } else {
//            if (!isServiceRunning(context, LocationService::class.java)){
//                context.startService(locationIntent)
//            }
//        }
        CoroutineScope(Dispatchers.IO).launch {
            val database = PhotoDatabase.getDatabase(context)
            val additionalPhotoDao = database.additionalPhotoDao()
            val additionalPhotoRepository = AdditionalPhotoRepository(additionalPhotoDao)
            additionalPhotoRepository.deleteAllAdditionalPhoto()
        }

    }

    Scaffold(
        bottomBar = {BottomNaviagtionBar(navHostController = navHostController)},
        backgroundColor = Color.White
    ) {
        MainNavGraph(
            navHostController = navHostController,
            photoForSyncView = photoForSyncView,
            syncedPhotoView =  syncedPhotoView,
            imageSyncView = imageSyncView
        )
    }
}