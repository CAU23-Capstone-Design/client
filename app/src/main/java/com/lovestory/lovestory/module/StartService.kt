package com.lovestory.lovestory.module

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lovestory.lovestory.model.LoginPayload
import com.lovestory.lovestory.services.LocationService
import java.util.*

/**
 * Start Service
 * 사용자 토큰이 있을 경우 로그인으로 간주하여 위치 서비스 실행한다.
 *
 * @param context Context
 */
@Composable
fun StartService(context : Context){

    val token = getToken(context)

    LaunchedEffect(key1 = null){
        if(token != null){
            val chunks: List<String> = token.split(".")
            val decoder: Base64.Decoder = Base64.getUrlDecoder()
            val payload = String(decoder.decode(chunks[1]))
            val payloadJSON : JsonObject = JsonParser.parseString(payload).asJsonObject
            val data = Gson().fromJson(payloadJSON, LoginPayload::class.java)

            if(data.couple != null){
                val intent = Intent(context, LocationService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            }
        }
    }
}