package com.lovestory.lovestory.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.lovestory.lovestory.ui.screens.CoupleSyncScreen
import com.lovestory.lovestory.ui.screens.LoginScreen

fun NavGraphBuilder.loginNavGraph(navHostController: NavHostController){
    navigation(
        route = Graph.AUTH,
        startDestination = AuthScreen.Login.route
    ){
        composable(route = AuthScreen.Login.route){
            LoginScreen(navHostController = navHostController)
        }
        composable(route = AuthScreen.CoupleSync.route+"/{code}&{nickname}"){backStackEntry ->
            val code = backStackEntry.arguments?.getString("code")
            val nickname = backStackEntry.arguments?.getString("nickname")
            CoupleSyncScreen(
//                onClick = {
//                    navHostController.popBackStack()
//                    navHostController.navigate(Graph.MAIN)
//                },
                navHostController = navHostController,
                myCode=code,
                nickname = nickname
            )
        }
    }
}

fun NavGraphBuilder.syncCiupleNavGraph(navHostController: NavHostController){
    navigation(
        route = Graph.AUTH,
        startDestination = AuthScreen.CoupleSync.route
    ){
        composable(route = AuthScreen.CoupleSync.route+"/{code}&{nickname}"){backStackEntry ->
            val code = backStackEntry.arguments?.getString("code")
            val nickname = backStackEntry.arguments?.getString("nickname")
            CoupleSyncScreen(
//                onClick = {
//                    navHostController.popBackStack()
//                    navHostController.navigate(Graph.MAIN)
//                },
                navHostController = navHostController,
                myCode=code,
                nickname = nickname
            )
        }
    }
}


sealed class AuthScreen(val route : String){
    object Login : AuthScreen(route = "login_screen")
    object CoupleSync : AuthScreen(route = "couplesync_screen")
}