package com.lovestory.lovestory.ui.screens

import android.graphics.BitmapFactory
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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import com.lovestory.lovestory.graphs.MainScreens
import com.lovestory.lovestory.model.averageLatLng
import com.lovestory.lovestory.model.points1

@Composable
fun MapScreen(navHostController: NavHostController){
    //val uri = Uri.parse("content://com.example.myapp/images/image.jpg")
    //val contentResolver = LocalContext.current.contentResolver
    //val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
    //val markerIcon = BitmapDescriptorFactory.fromBitmap(bitmap)

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
            points1.forEach { latLng ->
                val markerPositionState = rememberMarkerState(position = latLng)
                Marker(state = markerPositionState)//, icon = markerIcon)
            }
            Polyline(
                points = points1,
                color = Color.Black
            )
        }

        //Button(onClick = { navHostController.popBackStack() }) {
        //    Text(text = "Go Back")
        //}
    }
}