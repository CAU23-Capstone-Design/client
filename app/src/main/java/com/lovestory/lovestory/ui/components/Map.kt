package com.lovestory.lovestory.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import kotlin.random.Random



/*
@Composable
fun MapScreen() {
    val mapView = rememberMapViewWithLifecycle()

    val cameraPosition = remember {
        CameraPosition.Builder()
            .target(LatLng(37.5665, 126.9780)) // set Seoul as the viewpoint
            .zoom(12f)
            .build()
    }

    val polylineOptions = remember {
        PolylineOptions().apply {
            addAll(getRandomCoordinatesInSeoul()) // add random coordinates in Seoul to the polyline
        }
    }

    AndroidView(
        factory = { context ->
            mapView.apply {
                onCreate(null)
                onResume()
                getMapAsync { googleMap ->
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                    googleMap.addPolyline(polylineOptions)
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            onCreate(null)
            onResume()
        }
    }

    androidx.compose.runtime.onDispose {
        mapView.onPause()
        mapView.onStop()
        mapView.onDestroy()
    }

    return mapView
}

private fun getRandomCoordinatesInSeoul(): List<LatLng> {
    val seoulBounds = LatLngBounds(
        LatLng(37.413294, 126.764936),
        LatLng(37.701749, 127.183565)
    )

    return List(10) {
        LatLng(
            seoulBounds.southwest.latitude + Random.nextDouble() * (seoulBounds.northeast.latitude - seoulBounds.southwest.latitude),
            seoulBounds.southwest.longitude + Random.nextDouble() * (seoulBounds.northeast.longitude - seoulBounds.southwest.longitude)
        )
    }
}

 */