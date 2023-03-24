package com.lovestory.lovestory.ui.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import java.util.*


suspend fun testDelayFun(){
    delay(1000)
}

/** object for logic test */
data class UserTokenExample(
    var id: String,
    var name: String,
    var birthday: String,
    var gender: String,
    var code: String,
    var createAt: String
)

@Composable
fun Navigation(){
    Log.d("Navigation", "이 녀석은 언제 언제 나타는지 확인해보자")
    val navController = rememberNavController()

//    val isToken  = UserTokenExample(
//        id = "sdfsdf",
//        name = "Sdfsdf",
//        birthday = "fsdfsd",
//        gender = "ㅇㄴㄹㄴㅇㄹ",
//        code = "ㅇㄴㄹㄴㅇㄹ",
//        createAt = "ㅇㄴㄹㄴㅇㄹ"
//    )// 회원 token 확인 test 코드
    val isToken = null

    /** 회원 */
    if (isToken != null){

        /** 커플 연동한 회원 */
        if(isToken?.code != null){
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
        }
        /** 커플 연동 안된 회원 */
        else{
            NavHost(navController = navController, startDestination = Screen.CoupleSync.route ){
                composable(Screen.CoupleSync.route){
//                    CoupleSyncScreen(navHostController = navController)
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
            }
        }
    }
    /** 비회원 */
    else{
        NavHost(navController = navController, startDestination = Screen.Login.route){
            composable(Screen.Login.route){
                LoginScreen(navHostController = navController)
            }
            composable(Screen.SignUp.route+"/{id}&{nickname}&{gender}&{birthday}"){backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                val nickname = backStackEntry.arguments?.getString("nickname")
                val gender = backStackEntry.arguments?.getString("gender")
                val birthday = backStackEntry.arguments?.getString("birthday")
//                val thumbnailImageUrl = backStackEntry.arguments?.getString("thumbnailImageUrl")
//                SignUpScreen(navHostController = navController, id=id, nickname =nickname,gender=gender, thumbnailImageUrl =thumbnailImageUrl)
                SignUpScreen(navHostController = navController, id=id, nickname=nickname, gender=gender, birthday=birthday)
            }

            composable(Screen.CoupleSync.route+"/{id}&{code}&{nickname}"){backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                val code = backStackEntry.arguments?.getString("code")
                val nickname = backStackEntry.arguments?.getString("nickname")
                CoupleSyncScreen(navHostController = navController, id=id, myCode=code, nickname = nickname)
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
        }
    }
}

sealed class Screen(val route : String){
    object Login : Screen(route = "login_screen")
    object SignUp : Screen(route = "signup_screen")
    object CoupleSync : Screen(route = "couplesync_screen")
    object DashBoard : Screen(route = "dashboard_screen")
    object Gallery : Screen(route = "gallery_screen")
    object Calendar : Screen(route = "calendar_screen")
    object Profile : Screen(route = "profile_screen")
}