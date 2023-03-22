package com.lovestory.lovestory.module

import androidx.navigation.NavHostController
import com.lovestory.lovestory.ui.screens.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun LinkCouple(navHostController: NavHostController, userId : String, code : String, meetDay : String){
    CoroutineScope(Dispatchers.Main).launch{
        val response = createCouple(userId = userId, code = code, meetDay = meetDay)
        if(response.isSuccessful){
            navHostController.navigate(route = Screen.DashBoard.route){
                popUpTo(Screen.Login.route)
            }
        }else{
            // error 처리할게 없다.
        }
    }
}