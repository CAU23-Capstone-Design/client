package com.lovestory.lovestory.graphs

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lovestory.lovestory.R
import com.lovestory.lovestory.database.entities.SyncedPhoto
import com.lovestory.lovestory.ui.screens.*
import com.lovestory.lovestory.view.ImageSyncView
import com.lovestory.lovestory.view.PhotoForSyncView
import com.lovestory.lovestory.view.SyncedPhotoView
import com.squareup.moshi.Moshi

@Composable
fun MainNavGraph(
    navHostController: NavHostController,
    photoForSyncView: PhotoForSyncView,
    syncedPhotoView : SyncedPhotoView,
    imageSyncView : ImageSyncView
) {
//    val allPhotos by photoForSyncView.listOfPhotoForSync.observeAsState(initial = listOf())

    NavHost(
        navController = navHostController,
        startDestination = MainScreens.DashBoard.route,
        route = Graph.MAIN
    ) {
        composable(MainScreens.DashBoard.route) {
            DashBoardScreen(navHostController = navHostController)
        }
        composable(MainScreens.Gallery.route) {
            GalleryScreen(
                navHostController = navHostController,
                syncedPhotoView = syncedPhotoView
            )
        }
        composable(MainScreens.Calendar.route) {
            CalendarScreen(navHostController = navHostController, syncedPhotoView = syncedPhotoView)
        }
        composable(MainScreens.Profile.route) {
            ProfileScreen(navHostController = navHostController)
        }
        composable(GalleryStack.PhotoSync.route) {
            PhotoSyncScreen(
                navHostController = navHostController,
                photoForSyncView = photoForSyncView
            )
        }
        composable(GalleryStack.DetailPhotoFromDevice.route+"/{photoId}"){
            val photoId = it.arguments?.getString("photoId")
            PhotoDetailScreenFromDevice(navHostController = navHostController, photoId = photoId!!)
        }
        composable(GalleryStack.DetailPhotoFromServer.route+"/{photoIndex}"){
//            val photoId = it.arguments?.getString("photoId")
            val photoIndex = it.arguments?.getString("photoIndex")!!.toInt()
//            Log.d("naviagetion", "index : $indexForDetail")
            PhotoDetailScreenFromServer(
                navHostController = navHostController,
//                photoId = photoId!!,
                syncedPhotoView= syncedPhotoView,
                photoIndex = photoIndex
            )
        }
        composable(CalendarStack.Map.route + "/{date}") {
            val date = it.arguments?.getString("date")
            MapScreen(navHostController = navHostController, syncedPhotoView = syncedPhotoView, date = date!!)
        }
        composable(CalendarStack.DetailScreen.route+"/{photoIndex}/{date}"){
//            val photoId = it.arguments?.getString("photoId")
            val photoIndex = it.arguments?.getString("photoIndex")!!.toInt()
            val date = it.arguments?.getString("date")!!
//            Log.d("naviagetion", "index : $indexForDetail")
            CalendarPhotoDetailScreenFromServer(
                navHostController = navHostController,
                syncedPhotoView= syncedPhotoView,
                photoIndex = photoIndex,
                date = date
            )
        }
    }
}

sealed class CalendarStack(val route: String) {
    object Map : CalendarStack(route = "Map")

    object DetailScreen: CalendarStack(route = "Detail")
}

sealed class MainScreens(val route : String, val title : String, val icon : Int){
    object DashBoard : MainScreens(route = "DASHBOARD", title = "홈", icon = R.drawable.ic_home)
    object Gallery : MainScreens(route = "GALLERY", title = "갤러리", icon = R.drawable.ic_gallery)
    object Calendar : MainScreens(route = "CALENDAR", title = "캘린더", icon = R.drawable.ic_calendar)
    object Profile : MainScreens(route = "PROFILE", title= "프로필", icon = R.drawable.ic_setting)
    //object Map : MainScreens(route = "MAP", title = "MAP", icon = R.drawable.ic_option)
}

sealed class GalleryStack(val route : String){
    object PhotoSync : GalleryStack(route = "PhotoSync")
    object DetailPhotoFromDevice : GalleryStack(route= "DetailPhoto")

    object DetailPhotoFromServer : GalleryStack(route = "DetailPhotoFromServer")
}