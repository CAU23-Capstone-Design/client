package com.lovestory.lovestory

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lovestory.lovestory.graphs.RootNavigationGraph
import com.lovestory.lovestory.services.LocationService
import com.lovestory.lovestory.ui.theme.LoveStoryTheme
import com.lovestory.lovestory.ui.theme.LoveStoryThemeForMD3

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContent {
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = MaterialTheme.colors.isLight

//            val intent = Intent(this, LocationService::class.java)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(intent)
//            } else {
//                startService(intent)
//            }

            SideEffect {
                systemUiController.setSystemBarsColor(
                    color = Color.White,
                    darkIcons = useDarkIcons
                )
            }

            LoveStoryThemeForMD3() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    RootNavigationGraph()
                }
            }
        }
    }
}

@Composable
fun LoveStoryMainScreen() {
//    val navController = rememberNavController()
    Scaffold(
        bottomBar = { }
    ) {
        Box(Modifier.padding(it)){

        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun DefaultPreview() {
    LoveStoryTheme {
        RootNavigationGraph()
    }
}




