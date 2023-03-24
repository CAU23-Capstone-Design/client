package com.lovestory.lovestory.module

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.lovestory.lovestory.ui.screens.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun linkCouple(context : Context, navHostController: NavHostController, userId : String?, code : String?, meetDay : String?){
    navHostController.navigate(route = Screen.DashBoard.route){
        popUpTo(Screen.Login.route)
    }
    CoroutineScope(Dispatchers.Main).launch{
        val response = createCouple(userId = userId, code = code, meetDay = meetDay)
        if(response.isSuccessful){

            navHostController.navigate(route = Screen.DashBoard.route){
                popUpTo(Screen.Login.route)
            }
        }else{
            // error 처리할게 없다.
            Toast.makeText(context,"입력하신 코드가 올바른 코드가 아닙니다.", Toast.LENGTH_SHORT).show()
        }
    }
}