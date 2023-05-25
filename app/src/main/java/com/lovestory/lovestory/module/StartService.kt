package com.lovestory.lovestory.module

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startForegroundService
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.repository.AdditionalPhotoRepository
import com.lovestory.lovestory.model.LoginPayload
import com.lovestory.lovestory.services.LocationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import java.util.*

@Composable
fun StartService(context : Context){
    Log.d("StartService", "서비스 시작 결정")

    val token = getToken(context)

    LaunchedEffect(key1 = null){
        if(token != null){

            Log.d("StartService", "오 토큰이 있네")
            val chunks: List<String> = token.split(".")
            val decoder: Base64.Decoder = Base64.getUrlDecoder()
            val payload = String(decoder.decode(chunks[1]))
            val payloadJSON : JsonObject = JsonParser.parseString(payload).asJsonObject
            val data = Gson().fromJson(payloadJSON, LoginPayload::class.java)

            if(data.couple != null){
                Log.d("StartService", "서비스 시작")
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