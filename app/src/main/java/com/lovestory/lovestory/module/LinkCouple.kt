package com.lovestory.lovestory.module

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.lovestory.lovestory.graphs.Graph
import com.lovestory.lovestory.network.createCouple
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun linkCouple(context : Context, navHostController: NavHostController, code : String?, meetDay : String?){

    CoroutineScope(Dispatchers.Main).launch{
        val token : String? = getToken(context)

        if(token != null){
            val response = createCouple(token=token, code = code, meetDay = meetDay)
            if(response.isSuccessful){
                response.body()?.token?.let{
                    saveToken(context = context, it)
                }
                navHostController.navigate(route = Graph.MAIN){
                    navHostController.popBackStack()
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