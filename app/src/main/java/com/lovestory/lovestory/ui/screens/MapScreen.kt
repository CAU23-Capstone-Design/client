package com.lovestory.lovestory.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.lovestory.lovestory.graphs.MainScreens
import com.lovestory.lovestory.graphs.RootNavigationGraph
import com.lovestory.lovestory.model.averageLatLng
import com.lovestory.lovestory.model.points1
import com.lovestory.lovestory.ui.components.CustomMarker
import com.lovestory.lovestory.ui.theme.LoveStoryTheme

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
        val viewPosition = averageLatLng(points1)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(viewPosition!!, 18f)
        }
        val markerPositionState = rememberMarkerState(position = LatLng(37.503735330931136, 126.95615523253305))

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
        ) {
            //points1.forEach{ latLng ->
            //    val markerPositionState = rememberMarkerState(position = latLng)
            //    Marker(state = markerPositionState)
            //}

            Marker(
                state = markerPositionState,
                icon = CustomMarker(Uri.parse("file:///storage/emulated/0/DCIM/Camera/20230424_194354.jpg")),
                onClick = {
                    //isClicked = true
                    navHostController.popBackStack()
                    true
                }
            )
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

@Preview
@Composable
fun Preview(){
    val navController = rememberNavController()
    LoveStoryTheme() {
        MapScreen(navController)
    }
}