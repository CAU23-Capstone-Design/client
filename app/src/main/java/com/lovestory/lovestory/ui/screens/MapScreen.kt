package com.lovestory.lovestory.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.lovestory.lovestory.graphs.MainScreens
import com.lovestory.lovestory.model.averageLatLng
import com.lovestory.lovestory.model.points1

@Composable
fun MapScreen(navHostController: NavHostController){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val viewposition = averageLatLng(points1)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(viewposition!!, 15f)
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
        ) {
            Polyline(
                points = points1,
                color = Color.Black
            )
        }

        Button(onClick = { navHostController.popBackStack() }) {
            Text(text = "Go Back")
        }
    }
}