package com.lovestory.lovestory.module

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun loveStoryCheckCode(context : Context, onOpenDialogRequest :()->Unit, inputCode : String){
    CoroutineScope(Dispatchers.Main).launch {
        val response = checkValidCode(inputCode)
        if(response.isSuccessful){
            onOpenDialogRequest()
        }else{
            Toast.makeText(context,"입력하신 코드가 올바른 코드가 아닙니다.", Toast.LENGTH_LONG).show()
        }

    }

}