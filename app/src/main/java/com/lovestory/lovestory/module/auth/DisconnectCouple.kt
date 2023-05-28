package com.lovestory.lovestory.module.auth

import android.content.Context
import android.widget.Toast
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.network.deleteCouple
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun disconnectCouple(context: Context) {
    val token = getToken(context = context)

    CoroutineScope(Dispatchers.IO).launch{
        val response = deleteCouple(token)
        if(response == null) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "연결 끊기 과정 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }else{
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "다시 돌아올 때까지 기다릴게요ㅠㅠ", Toast.LENGTH_SHORT).show()
            }
        }
    }
}