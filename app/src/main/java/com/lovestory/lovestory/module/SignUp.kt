package com.lovestory.lovestory.module

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.lovestory.lovestory.ui.screens.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun loveStorySignUp(
    navHostController: NavHostController,
    id:String?,
    nickname:String,
    gender:String,
    birthday:String,
    context : Context
){
    CoroutineScope(Dispatchers.Main).launch{
        val response = createUser(id = id?.toLong(), name = nickname, birthday = birthday, gender = gender)
        if(response.isSuccessful){
            navHostController.navigate(route = Screen.CoupleSync.route+"/$id&${response.body()?.code}&${response.body()?.name}"){
                popUpTo(Screen.Login.route)
            }
        }else{
            Toast.makeText(context,"오류가 발생했습니다.\n잠시후 다시 시도해 주세요",Toast.LENGTH_LONG).show()
        }
    }
}