package com.lovestory.lovestory.graphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lovestory.lovestory.R
import com.lovestory.lovestory.ui.screens.*

@Composable
fun MainNavGraph(navHostController: NavHostController){
    NavHost(navController = navHostController, startDestination =MainScreens.DashBoard.route, route = Graph.MAIN){
        composable(MainScreens.DashBoard.route){
            DashBoardScreen(navHostController = navHostController)
        }
        composable(MainScreens.Gallery.route){
            GalleryScreen(navHostController = navHostController)
        }
        composable(MainScreens.Calendar.route){
            CalendarScreen(navHostController = navHostController)
        }
        composable(MainScreens.Profile.route){
            ProfileScreen(navHostController = navHostController)
        }
    }
}


sealed class MainScreens(val route : String, val title : String, val icon : Int){
    object DashBoard : MainScreens(route = "DASHBOARD", title = "홈", icon = R.drawable.ic_home)
    object Gallery : MainScreens(route = "GALLERY", title = "갤러리", icon = R.drawable.ic_gallery)
    object Calendar : MainScreens(route = "CALENDAR", title = "캘린더", icon = R.drawable.ic_calendar)
    object Profile : MainScreens(route = "PROFILE", title= "프로필", icon = R.drawable.ic_setting)
}