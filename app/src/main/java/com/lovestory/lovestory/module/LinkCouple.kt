package com.lovestory.lovestory.module

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavHostController
import com.lovestory.lovestory.network.createCouple
import com.lovestory.lovestory.ui.screens.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun linkCouple(context : Context, navHostController: NavHostController, code : String?, meetDay : String?){

//    navHostController.navigate(route = Screen.DashBoard.route){
//        popUpTo(Screen.Login.route)
//    }

    CoroutineScope(Dispatchers.Main).launch{
        val token : String? = getToken(context)
        Log.d("link couple", "$token")

        if(token != null){
            val response = createCouple(token=token, code = code, meetDay = meetDay)
            if(response.isSuccessful){
                Log.d("success make couple", "it")
                response.body()?.token?.let{
                    Log.d("success make couple", "it")
                    saveToken(context = context, it)
                }
                navHostController.navigate(route = Screen.DashBoard.route){
                    popUpTo(Screen.Login.route)
                }
            }
            else{
                // error 처리할게 없다.
                Toast.makeText(context,"입력하신 코드가 올바른 코드가 아닙니다.", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(context,"사용자 정보가 없습니다. ERROR.", Toast.LENGTH_SHORT).show()
        }

    }
}