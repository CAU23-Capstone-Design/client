package com.lovestory.lovestory.graphs

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lovestory.lovestory.model.LoginPayload
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.ui.screens.DashBoardScreen
import java.util.*

@Composable
fun RootNavigationGraph(){
    Log.d("Root Navigation", "Root Navigation called")
    val navHostController = rememberNavController()
    val context = LocalContext.current

    val token = getToken(context)

    if(token != null){
        val chunks: List<String> = token.split(".")
        val decoder: Base64.Decoder = Base64.getUrlDecoder()
        val payload = String(decoder.decode(chunks[1]))
        val payloadJSON : JsonObject = JsonParser.parseString(payload).asJsonObject
        val data = Gson().fromJson(payloadJSON, LoginPayload::class.java)

        if(data.couple != null){
            NavHost(
                navController = navHostController,
                route = Graph.ROOT,
                startDestination = Graph.MAIN
            ) {
                composable(route = Graph.MAIN) {
                    DashBoardScreen(navHostController = navHostController)
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
                composable(route = Graph.MAIN) {
                    DashBoardScreen(navHostController = navHostController)
                }
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
            composable(route = Graph.MAIN) {
                DashBoardScreen(navHostController = navHostController)
            }
        }
    }
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTH = "auth_graph"
    const val MAIN = "main_graph"
}