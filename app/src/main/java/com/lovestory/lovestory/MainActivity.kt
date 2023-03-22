package com.lovestory.lovestory

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lovestory.lovestory.ui.screens.Navigation
import com.lovestory.lovestory.ui.theme.LoveStoryTheme
import com.lovestory.lovestory.ui.theme.LoveStoryThemeForMD3

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContent {
            LoveStoryThemeForMD3() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    LoveStoryMainScreen()
                }
            }
        }
    }
}

@Composable
fun LoveStoryMainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { }
    ) {
        Box(Modifier.padding(it)){
            Navigation()
        }
    }
}






