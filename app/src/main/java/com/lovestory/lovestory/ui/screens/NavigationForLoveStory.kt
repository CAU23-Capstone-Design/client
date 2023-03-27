package com.lovestory.lovestory.ui.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lovestory.lovestory.model.LoginPayload
import com.lovestory.lovestory.module.getToken
import java.util.*

@Composable
fun Navigation(){
    Log.d("Navigation", "Navigation called")
    val navController = rememberNavController()
    val context = LocalContext.current

    val token = getToken(context)

    if(token != null){
        val chunks: List<String> = token.split(".")
        val decoder: Base64.Decoder = Base64.getUrlDecoder()
        val payload = String(decoder.decode(chunks[1]))
        val payloadJSON : JsonObject = JsonParser.parseString(payload).asJsonObject
        val data = Gson().fromJson(payloadJSON, LoginPayload::class.java)

        if(data.couple != null){
            Log.d("Navigation", "커플 회원!")
            NavHost(navController = navController, startDestination = Screen.DashBoard.route ){
                composable(Screen.DashBoard.route){
                    DashBoardScreen(navHostController = navController)
                }
                composable(Screen.Gallery.route){
                    GalleryScreen(navHostController = navController)
                }
                composable(Screen.Calendar.route){
                    CalendarScreen(navHostController = navController)
                }
                composable(Screen.Profile.route){
                    ProfileScreen(navHostController = navController)
                }
            }
        }else{
            Log.d("Navigation", "비 커플 회원!")
            GetNonCoupleNavigate(navController)
        }
    }
    else{
        Log.d("Navigation", "비 회원!")
        GetNonCoupleNavigate(navController)
    }
}

@Composable
fun GetNonCoupleNavigate(navController : NavHostController){
    return (NavHost(navController = navController, startDestination = Screen.Login.route){
        composable(Screen.Login.route){
            LoginScreen(navHostController = navController)
        }
        composable(Screen.CoupleSync.route+"/{code}&{nickname}"){backStackEntry ->
            val code = backStackEntry.arguments?.getString("code")
            val nickname = backStackEntry.arguments?.getString("nickname")
            CoupleSyncScreen(navHostController = navController, myCode=code, nickname = nickname)
        }
        composable(Screen.DashBoard.route){
            DashBoardScreen(navHostController = navController)
        }
        composable(Screen.Gallery.route){
            GalleryScreen(navHostController = navController)
        }
        composable(Screen.Calendar.route){
            CalendarScreen(navHostController = navController)
        }
        composable(Screen.Profile.route){
            ProfileScreen(navHostController = navController)
        }
    })
}

sealed class Screen(val route : String){
    object Login : Screen(route = "login_screen")
    object CoupleSync : Screen(route = "couplesync_screen")
    object DashBoard : Screen(route = "dashboard_screen")
    object Gallery : Screen(route = "gallery_screen")
    object Calendar : Screen(route = "calendar_screen")
    object Profile : Screen(route = "profile_screen")
}