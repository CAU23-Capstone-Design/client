package com.lovestory.lovestory.ui.screens

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.*
import com.lovestory.lovestory.ui.components.CustomMarker
import com.lovestory.lovestory.ui.theme.LoveStoryTheme
import com.google.maps.android.compose.clustering.Clustering
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.lovestory.lovestory.R
import com.lovestory.lovestory.model.*
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.network.getGps
import com.lovestory.lovestory.ui.components.toInt


@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapScreen(navHostController: NavHostController, date: String){
    val context = LocalContext.current
    val token = getToken(context)
    val dataLoaded = remember { mutableStateOf(false) }
    var latLng by remember { mutableStateOf(emptyList<LatLng>()) }
    val items = remember{ mutableStateListOf<MyItem>() }
    var viewPosition = LatLng(37.503735330931136, 126.95615523253305)
    var cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(viewPosition!!, 18f)
    }
    val drawable1 = ContextCompat.getDrawable(context, R.drawable.img)
    val bitmap1 = (drawable1 as BitmapDrawable).bitmap

    LaunchedEffect(true){
        //get GPS
        val gps = getGps(token!!, date)
        latLng = if (gps.body() != null) {
            getLatLng(gps.body()!!)
        }else{
            val position = mutableListOf<LatLng>()
            position.add(LatLng(37.503735330931136, 126.95615523253305))
            position
        }
        dataLoaded.value = true

        //사진 좌표와 비트맵
        points1.forEach{
            items.add(MyItem(it,"Marker","Snippet", bitmap1))
            //latLng = latLng + it
            Log.d("위치좌표","$latLng")
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!dataLoaded.value) {
            //스켈레톤 추가
            Box(modifier = Modifier.fillMaxSize().background(color = Color.Transparent))
        } else {
            viewPosition = averageLatLng(latLng)
            cameraPositionState = CameraPositionState(position = CameraPosition.fromLatLngZoom(viewPosition, 15f))

            val zoomLevel = getZoomLevelForDistance(getMaxDistanceBetweenLatLng(viewPosition, latLng)) - 1
            cameraPositionState = remember { CameraPositionState(position = CameraPosition.fromLatLngZoom(viewPosition, zoomLevel)) }

//            latLng.forEach{latLng ->
//                items.add(MyItem(latLng, "Marker","Snippet",bitmap1))
//            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
            ) {
                Clustering(
                    items = items,
                    // Optional: Handle clicks on clusters, cluster items, and cluster item info windows
                    onClusterClick = {
                        Log.d("TAG", "Cluster clicked! $it") // 클러스터 클릭했을 때
                        false
                    },
                    onClusterItemClick = {
                        Log.d("TAG", "Cluster item clicked! $it") // 클러스팅 되지 않은 마커 클릭했을 때
                        false
                    },
                    onClusterItemInfoWindowClick = {
                        Log.d("TAG", "Cluster item info window clicked! $it") // 클러스팅 되지 않은 마커의 정보창을 클릭했을 때
                    },
                    // Optional: Custom rendering for clusters
                    clusterContent = { cluster ->
                        Log.d("클러스터","${cluster.size}")
                        //Log.d("클러스터","${cluster.items.first().icon}")
                        val drawable = ContextCompat.getDrawable(context, R.drawable.img)
                        val bitmap = (drawable as BitmapDrawable).bitmap
                        val size = 50.dp
                        val scaledBitmap = bitmap?.let {
                            val density = LocalDensity.current.density
                            val scaledSize = (size * density).toInt()
                            Bitmap.createScaledBitmap(it, scaledSize, scaledSize, false)
                        }!!.asImageBitmap()
                        Surface(
                            shape = RoundedCornerShape(percent = 10),
                            contentColor = Color.White,
                            border = BorderStroke(1.dp, Color.White),
                            elevation = 10.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    bitmap = scaledBitmap,
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp)
                                )
                                Text(
                                    "%,d".format(cluster.size), //이 부분 왜 2배로 나오지..?
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    },
                    // Optional: Custom rendering for non-clustered items
                    clusterItemContent = { item ->
                        Log.d("아이템", "$item")
                        val drawable = ContextCompat.getDrawable(context, R.drawable.img)
                        val bitmap = (drawable as BitmapDrawable).bitmap
                        val size = 50.dp
                        val scaledBitmap = bitmap?.let {
                            val density = LocalDensity.current.density
                            val scaledSize = (size * density).toInt()
                            Bitmap.createScaledBitmap(it, scaledSize, scaledSize, false)
                        }!!.asImageBitmap()
                        Surface(
                            shape = RoundedCornerShape(percent = 10),
                            contentColor = Color.White,
                            border = BorderStroke(1.dp, Color.White),
                            elevation = 10.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    bitmap = scaledBitmap,
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}


data class MyItem(
    val itemPosition: LatLng,
    val itemTitle: String,
    val itemSnippet: String,
    val icon: Bitmap,
) : ClusterItem {
    override fun getPosition(): LatLng =
        itemPosition

    override fun getTitle(): String =
        itemTitle

    override fun getSnippet(): String =
        itemSnippet

//    fun getIcon(): BitmapDescriptor {
//        return icon
//    }
}

@Preview
@Composable
fun Preview(){
    val navController = rememberNavController()
    LoveStoryTheme() {
    }
}