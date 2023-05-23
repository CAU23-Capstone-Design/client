package com.lovestory.lovestory.graphs

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lovestory.lovestory.R
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
){

    NavHost(
        navController = navHostController,
        startDestination = MainScreens.DashBoard.route,
        route = Graph.MAIN
    ) {
        composable(MainScreens.DashBoard.route){
            DashBoardScreen(navHostController = navHostController)
        }
        composable(MainScreens.Gallery.route){
            GalleryScreen(
                navHostController = navHostController,
                syncedPhotoView = syncedPhotoView
            )
        }
        composable(MainScreens.Calendar.route){
            CalendarScreen(navHostController = navHostController, syncedPhotoView = syncedPhotoView)
        }
        composable(MainScreens.Profile.route){
            ProfileScreen(navHostController = navHostController)
        }
        composable(GalleryStack.PhotoSync.route){
            PhotoSyncScreen(
                navHostController = navHostController,
                photoForSyncView = photoForSyncView
            )
        }
        composable(GalleryStack.DetailPhotoFromDevice.route+"/{photoId}"){
            val photoId = it.arguments?.getString("photoId")
            PhotoDetailScreenFromDevice(
                navHostController = navHostController,
                photoId = photoId!!
            )
        }
        composable(GalleryStack.DetailPhotoFromDeviceWithPicker.route+"/{photoId}"){
            val photoId = it.arguments?.getString("photoId")
            PhotoDetailScreenFromDeviceWithMediaPicker(navHostController = navHostController, photoId = photoId!!)
        }
        composable(GalleryStack.DetailPhotoFromServer.route+"/{photoIndex}"){
            val photoIndex = it.arguments?.getString("photoIndex")!!.toInt()
            PhotoDetailScreenFromServer(
                navHostController = navHostController,
                syncedPhotoView= syncedPhotoView,
                photoIndex = photoIndex
            )
        }
        composable(CalendarStack.Map.route + "/{date}"){
            val date = it.arguments?.getString("date")
            MapScreen(navHostController = navHostController, syncedPhotoView = syncedPhotoView, date = date!!)
        }
        composable(CalendarStack.DetailScreen.route+"/{photoIndex}/{date}"){
            val photoIndex = it.arguments?.getString("photoIndex")!!.toInt()
            val date = it.arguments?.getString("date")!!
            CalendarPhotoDetailScreenFromServer(
                navHostController = navHostController,
                syncedPhotoView= syncedPhotoView,
                photoIndex = photoIndex,
                date = date
            )
        }
        composable(CalendarStack.ClickDetailScreen.route+"/{id}/{date}"){
            val id = it.arguments?.getString("id")!!
            val date = it.arguments?.getString("date")!!
            ClickPhotoDetailScreenFromServer(
                navHostController = navHostController,
                syncedPhotoView= syncedPhotoView,
                id = id,
                date = date
            )
        }
        composable(ProfileStack.Help.route){
            HelpScreen(navHostController = navHostController)
        }
        composable(ProfileStack.Privacy.route){
            PrivacyScreen(navHostController = navHostController)
        }
    }
}

sealed class MainScreens(val route : String, val title : String, val icon : Int){
    object DashBoard : MainScreens(route = "DASHBOARD", title = "홈", icon = R.drawable.ic_home)
    object Gallery : MainScreens(route = "GALLERY", title = "갤러리", icon = R.drawable.ic_gallery)
    object Calendar : MainScreens(route = "CALENDAR", title = "캘린더", icon = R.drawable.ic_calendar)
    object Profile : MainScreens(route = "PROFILE", title= "프로필", icon = R.drawable.ic_setting)
}

sealed class GalleryStack(val route : String){
    object PhotoSync : GalleryStack(route = "PhotoSync")
    object DetailPhotoFromDevice : GalleryStack(route= "DetailPhoto")

    object DetailPhotoFromDeviceWithPicker : GalleryStack(route="DetailPhotoWithPicker")

    object DetailPhotoFromServer : GalleryStack(route = "DetailPhotoFromServer")
}

sealed class CalendarStack(val route: String) {
    object Map : CalendarStack(route = "Map")
    object DetailScreen: CalendarStack(route = "Detail")
    object ClickDetailScreen: CalendarStack(route = "Click")
}

sealed class ProfileStack(val route: String) {
    object Help : ProfileStack(route = "Help")
    object Privacy: ProfileStack(route = "Privacy")
}