package com.lovestory.lovestory.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.compose.*
import com.google.maps.android.ui.IconGenerator
import com.lovestory.lovestory.model.averageLatLng
import com.lovestory.lovestory.model.points1
import com.lovestory.lovestory.ui.components.CustomMarker
import com.lovestory.lovestory.ui.theme.LoveStoryTheme
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import com.google.maps.android.compose.clustering.Clustering
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.lovestory.lovestory.ui.components.toInt
import java.io.File


@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapScreen(navHostController: NavHostController){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val viewPosition = averageLatLng(points1)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(viewPosition!!, 18f)
        }
        val items = remember{ mutableStateListOf<MyItem>() }
        //LaunchedEffect(true){
            points1.forEach{ latLng ->
                items.add(MyItem(latLng,"Marker","Snippet", CustomMarker(Uri.parse("file:///storage/emulated/0/DCIM/Camera/20230424_194354.jpg"))))
            }
        //}

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
        ) {
            val context = LocalContext.current

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
                    val context = LocalContext.current

                    val drawable = ContextCompat.getDrawable(context, R.drawable.img)
                    val bitmap = (drawable as BitmapDrawable).bitmap

                    val bitmap1 = context.contentResolver.openFileDescriptor(Uri.parse("file:///storage/emulated/0/DCIM/Camera/20230424_194354.jpg"), "r")?.use { descriptor ->
                        BitmapFactory.decodeFileDescriptor(descriptor.fileDescriptor)
                    }
                    val size = 50.dp
                    val scaledBitmap = bitmap?.let {
                        val density = LocalDensity.current.density
                        val scaledSize = (size * density).toInt()
                        Bitmap.createScaledBitmap(it, scaledSize, scaledSize, false)
                    }!!.asImageBitmap()
                    Image(
                        bitmap = scaledBitmap,
                        contentDescription = "Marker Image",
                        modifier = Modifier.size(40.dp)
                    )
                    /*Surface(
                        Modifier.size(40.dp),
                        shape = CircleShape,
                        color = Color.Blue,
                        contentColor = Color.White,
                        border = BorderStroke(1.dp, Color.White)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "%,d".format(cluster.size/2), //이 부분 왜 2배로 나오지..?
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center
                            )
                        }
                    }*/
                },
                // Optional: Custom rendering for non-clustered items
                clusterItemContent = null
            )
            /*
            var clusterManager by remember { mutableStateOf<ClusterManager<MyItem>?>(null) }
            MapEffect(items) { map ->
                if (clusterManager == null) {
                    clusterManager = ClusterManager<MyItem>(context, map)
                }
                clusterManager?.addItems(items)
                clusterManager?.renderer = MarkerClusterRender(context,map,clusterManager!!) {
                    //showToast(it.itemTitle)
                }
            }
            LaunchedEffect(key1 = cameraPositionState.isMoving) {
                if (!cameraPositionState.isMoving) {
                    clusterManager?.onCameraIdle()
                }
            }
            */

            //points1.forEach{ latLng ->

            //    val markerPositionState = rememberMarkerState(position = latLng)
            //    Marker(state = markerPositionState)
            //}

            //Marker(
            //    state = markerPositionState,
            //    icon = CustomMarker(Uri.parse("file:///storage/emulated/0/DCIM/Camera/20230424_194354.jpg")),
            //    onClick = {
                    //isClicked = true
            //        navHostController.popBackStack()
            //       true
            //    }
            //)
            //Polyline(
            //    points = points1,
            //    color = Color.Black
            //)
        }
    }
}

class MarkerClusterRender <T : ClusterItem>(
    var context: Context,
    var googleMap: GoogleMap,
    clusterManager: ClusterManager<T>,
    var onInfoWindowClick: (MyItem) -> Unit
) :
    DefaultClusterRenderer<T>(context, googleMap, clusterManager) {

    private var clusterMap: HashMap<String, Marker> = hashMapOf()

    override fun shouldRenderAsCluster(cluster: Cluster<T>): Boolean {
        return cluster.size > 1
    }

    override fun getBucket(cluster: Cluster<T>): Int {
        return cluster.size
    }

    override fun getClusterText(bucket: Int): String {
        return super.getClusterText(bucket).replace("+", "")
    }

    override fun onClusterItemRendered(clusterItem: T, marker: Marker) {
        super.onClusterItemRendered(clusterItem, marker)
        clusterMap[(clusterItem as MyItem).itemTitle] = marker

        setMarker((clusterItem as MyItem), marker)
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun setMarker(poi: MyItem, marker: Marker?) {
        val markerColor = BitmapDescriptorFactory.HUE_RED
        marker?.let {
            it.tag = poi
            it.showInfoWindow()
            changeMarkerColor(it, markerColor)
        }
        googleMap.setOnInfoWindowClickListener {
            onInfoWindowClick(it.tag as MyItem)
        }
    }

    private fun getClusterMarker(itemId: String): Marker? {
        return if (clusterMap.containsKey(itemId)) clusterMap[itemId]
        else null
    }


    fun showRouteInfoWindow(key: String) {
        getClusterMarker(key)?.showInfoWindow()
    }

    private fun changeMarkerColor(marker: Marker, color: Float) {
        try {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(color));
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}
/*
data class CustomClusterItem(
    val position: LatLng,
    val title: String?,
    val snippet: String?
) : ClusterItem {
    override fun getPosition(): LatLng = position
    override fun getTitle(): String? = title
    override fun getSnippet(): String? = snippet
}

 */

data class MyItem(
    val itemPosition: LatLng,
    val itemTitle: String,
    val itemSnippet: String,
    val icon: BitmapDescriptor,
) : ClusterItem {
    override fun getPosition(): LatLng =
        itemPosition

    override fun getTitle(): String =
        itemTitle

    override fun getSnippet(): String =
        itemSnippet

    //fun getIcon(): BitmapDescriptor {
    //    return icon
    //}
}

@Preview
@Composable
fun Preview(){
    val navController = rememberNavController()
    LoveStoryTheme() {
        MapScreen(navController)
    }
}