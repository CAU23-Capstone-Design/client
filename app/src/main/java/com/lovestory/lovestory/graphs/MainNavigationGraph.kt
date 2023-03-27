package com.lovestory.lovestory.graphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lovestory.lovestory.ui.screens.CalendarScreen
import com.lovestory.lovestory.ui.screens.DashBoardScreen
import com.lovestory.lovestory.ui.screens.GalleryScreen
import com.lovestory.lovestory.ui.screens.ProfileScreen

@Composable
fun MainNavGraph(navHostController: NavHostController){
    NavHost(navController = navHostController, startDestination =MainScreen.DashBoard.route, route = Graph.MAIN){
        composable(Screen.DashBoard.route){
            DashBoardScreen(navHostController = navHostController)
        }
        composable(Screen.Gallery.route){
            GalleryScreen(navHostController = navHostController)
        }
        composable(Screen.Calendar.route){
            CalendarScreen(navHostController = navHostController)
        }
        composable(Screen.Profile.route){
            ProfileScreen(navHostController = navHostController)
        }
    }
}


sealed class MainScreen(val route : String){
    object DashBoard : MainScreen(route = "dashboard_screen")
    object Gallery : MainScreen(route = "gallery_screen")
    object Calendar : MainScreen(route = "calendar_screen")
    object Profile : MainScreen(route = "profile_screen")
}