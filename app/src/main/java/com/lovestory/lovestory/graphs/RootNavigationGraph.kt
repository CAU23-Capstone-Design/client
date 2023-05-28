package com.lovestory.lovestory.graphs

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.lovestory.lovestory.module.checkExistNeedPhotoForSync
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.module.getTokenInfo
import com.lovestory.lovestory.ui.screens.MainScreen

const val tagName = "[NAVIGATION]ROOT"

@Composable
fun RootNavigationGraph(){
    Log.d(tagName, "Root Navigation called")
    val navHostController = rememberNavController()
    val context = LocalContext.current

    val token = getToken(context)

    if(token != null){
        Log.d(tagName, "$token")

        val data = getTokenInfo(token)

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
                composable(route = Graph.MAIN) {MainScreen(userData = data.user)}
            }
        }
        else{
            NavHost(
                navController = navHostController,
                route = Graph.ROOT,
                startDestination = Graph.AUTH
            ) {
                loginNavGraph(navHostController = navHostController)
                composable(route = Graph.MAIN) {MainScreen(userData = data.user)}
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