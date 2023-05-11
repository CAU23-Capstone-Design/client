package com.lovestory.lovestory.graphs

import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lovestory.lovestory.model.LoginPayload
import com.lovestory.lovestory.module.checkExistNeedPhotoForSync
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.services.LocationService
import com.lovestory.lovestory.ui.screens.MainScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@Composable
fun RootNavigationGraph(){
    Log.d("Root Navigation", "Root Navigation called")
    val navHostController = rememberNavController()
    val context = LocalContext.current

    val token = getToken(context)

    if(token != null){
        Log.d("RootNavigationGraph", "$token")
        val chunks: List<String> = token.split(".")
        val decoder: Base64.Decoder = Base64.getUrlDecoder()
        val payload = String(decoder.decode(chunks[1]))
        val payloadJSON : JsonObject = JsonParser.parseString(payload).asJsonObject
        val data = Gson().fromJson(payloadJSON, LoginPayload::class.java)

        Log.d("LoveStory Token", "$data")

        if(data.couple != null){
            LaunchedEffect(true){
                CoroutineScope(Dispatchers.IO).launch {
                    checkExistNeedPhotoForSync(context)
                }
            }
            NavHost(
                navController = navHostController,
                route = Graph.ROOT,
                startDestination = Graph.MAIN
            ) {
                composable(route = Graph.MAIN) {MainScreen()}
            }
        }
        else{
            NavHost(
                navController = navHostController,
                route = Graph.ROOT,
                startDestination = Graph.AUTH
            ) {
                loginNavGraph(navHostController = navHostController)
                composable(route = Graph.MAIN) {MainScreen()}
            }
        }
    }
    else{
        NavHost(
            navController = navHostController,
            route = Graph.ROOT,
            startDestination = Graph.AUTH
        ) {
            loginNavGraph(navHostController = navHostController)
            composable(route = Graph.MAIN) {MainScreen()}
        }
    }
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTH = "auth_graph"
    const val MAIN = "main_graph"
}