package com.lovestory.lovestory.graphs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lovestory.lovestory.R
import com.lovestory.lovestory.ui.screens.*
import com.lovestory.lovestory.view.PhotoViewModel

@Composable
fun MainNavGraph(navHostController: NavHostController, viewModel : PhotoViewModel){
    val allPhotos by viewModel.allPhotos.observeAsState(initial = listOf())

    NavHost(navController = navHostController, startDestination =MainScreens.DashBoard.route, route = Graph.MAIN){
        composable(MainScreens.DashBoard.route){
            DashBoardScreen(navHostController = navHostController)
        }
        composable(MainScreens.Gallery.route){
            GalleryScreen(navHostController = navHostController, viewModel = viewModel, allPhotos = allPhotos)
        }
        composable(MainScreens.Calendar.route){
            CalendarScreen(navHostController = navHostController)
        }
        composable(MainScreens.Profile.route){
            ProfileScreen(navHostController = navHostController)
        }
        composable(GalleryStack.PhotoSync.route){
            PhotoSyncScreen(navHostController = navHostController, viewModel = viewModel, allPhotos = allPhotos)
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
}