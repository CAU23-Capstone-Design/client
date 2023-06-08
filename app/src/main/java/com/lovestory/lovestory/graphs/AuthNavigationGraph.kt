package com.lovestory.lovestory.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.lovestory.lovestory.ui.screens.CoupleSyncScreen
import com.lovestory.lovestory.ui.screens.LoginScreen

/**
 * 로그인 네비게이션 그래프
 *
 * @param navHostController 네비게이션 컨트롤러
 */
fun NavGraphBuilder.loginNavGraph(navHostController: NavHostController){
    navigation(
        route = Graph.AUTH,
        startDestination = AuthScreen.Login.route
    ){
        composable(route = AuthScreen.Login.route){
            LoginScreen(navHostController = navHostController)
        }
        composable(route = AuthScreen.CoupleSync.route+"/{code}&{nickname}&{gender}"){backStackEntry ->
            val code = backStackEntry.arguments?.getString("code")
            val nickname = backStackEntry.arguments?.getString("nickname")
            val gender =  backStackEntry.arguments?.getString("gender")
            CoupleSyncScreen(
                navHostController = navHostController,
                myCode=code,
                nickname = nickname,
                gender = gender
            )
        }
    }
}

sealed class AuthScreen(val route : String){
    object Login : AuthScreen(route = "login_screen")
    object CoupleSync : AuthScreen(route = "couplesync_screen")
}