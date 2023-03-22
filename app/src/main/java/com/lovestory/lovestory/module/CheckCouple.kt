package com.lovestory.lovestory.module

import android.util.Log
import androidx.navigation.NavHostController
import com.lovestory.lovestory.ui.screens.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun loveStoryCheckCouple(navHostController: NavHostController, id : String?){
    Log.d("checkCouple-id","$id")
    CoroutineScope(Dispatchers.Main).launch{
        var success = false
        while(!success){
            delay(5000)
            try{
                val response =  checkCouple(id)
                if (response.isSuccessful) {
                    success = true
                    // handle successful response
                }else{
                    Log.d("checkCouple", "$response")
                }
            }catch (e : Exception){

            }
        }
        navHostController.navigate(route = Screen.DashBoard.route){
            popUpTo(Screen.Login.route)
        }
    }

}