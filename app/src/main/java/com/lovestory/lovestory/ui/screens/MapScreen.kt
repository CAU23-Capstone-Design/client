package com.lovestory.lovestory.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.*
import com.lovestory.lovestory.ui.theme.LoveStoryTheme
import com.google.maps.android.compose.clustering.Clustering
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.lovestory.lovestory.R
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.entities.SyncedPhoto
import com.lovestory.lovestory.database.repository.SyncedPhotoRepository
import com.lovestory.lovestory.graphs.CalendarStack
import com.lovestory.lovestory.graphs.MainScreens
import com.lovestory.lovestory.model.*
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.module.loadBitmapFromDiskCache
import com.lovestory.lovestory.module.photo.getThumbnailForPhoto
import com.lovestory.lovestory.module.saveBitmapToDiskCache
import com.lovestory.lovestory.network.getGps
import com.lovestory.lovestory.network.getPhotoTable
import com.lovestory.lovestory.ui.components.*
import com.lovestory.lovestory.view.SyncedPhotoView
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation")
@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapScreen(navHostController: NavHostController, syncedPhotoView : SyncedPhotoView, date: String){
    val context = LocalContext.current
    val token = getToken(context)
    val dataLoaded = remember { mutableStateOf(false) }
    var latLng by remember { mutableStateOf(emptyList<LatLng>()) }
    var latLngMarker by remember { mutableStateOf(emptyList<LatLng>()) }
    val items = remember{ mutableStateListOf<MyItem>() }
    val items_google = remember{ mutableStateListOf<MyItem>() }
//    var viewPosition = LatLng(37.503735330931136, 126.95615523253305)
//    var cameraPositionState = rememberCameraPositionState {
//        position = CameraPosition.fromLatLngZoom(viewPosition!!, 18f)
//    }
    lateinit var repository : SyncedPhotoRepository
    var photoDate by remember { mutableStateOf(emptyList<String>()) }
    var photoPosition by remember { mutableStateOf(emptyList<LatLng>()) }

    var bitmapList by remember { mutableStateOf(emptyList<Bitmap>()) }

    val drawable1 = ContextCompat.getDrawable(context, R.drawable.img)
    val bitmap1 = (drawable1 as BitmapDrawable).bitmap

    var isPopupVisible by remember { mutableStateOf(false) }
    var itemPopup by remember { mutableStateOf(emptyList<MyItem>()) }

    val allPhotoListState = rememberLazyListState()

    val syncedPhotosByDate by syncedPhotoView.groupedSyncedPhotosByDate.observeAsState(initial = mapOf())

    val systemUiController = rememberSystemUiController()

    var syncedPhoto by remember { mutableStateOf(emptyList<SyncedPhoto>()) }

    val coroutineScopeMap = rememberCoroutineScope()

    BackHandler(enabled = true) {
        coroutineScopeMap.cancel()
        navHostController.popBackStack()
    }

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF3F3F3),
        )
    }

    LaunchedEffect(true){
        //get GPS
        val gps = getGps(token!!, date)
        if (gps.body() != null) {
            latLng = getLatLng(gps.body()!!)
        }
        latLngMarker = latLng

        val photoDatabase = PhotoDatabase.getDatabase(context)
        val photoDao = photoDatabase.syncedPhotoDao()
        repository = SyncedPhotoRepository(photoDao)

        syncedPhoto = repository.getSyncedPhotosByDate(date)

        photoPosition = syncedPhoto.map { it ->
            LatLng(it.latitude, it.longitude)
        }

//        photoDate = syncedPhoto.map{
//            it.date
//        }

        /*
        syncedPhoto.forEach{
            items.add(MyItem(LatLng(it.latitude, it.longitude), "Marker1", "사진",
                bitmap1//getThumbnailForPhoto(token!!, it.id)!!
                , "PHOTO", it.id))
        }

        //사진 좌표와 비트맵
        latLng.forEach{
            items.add(MyItem(it,"Marker2","마커", bitmap1, "POSITION", "HI"))
        }
        */

        photoPosition.forEach {
            latLng += it
        }

        dataLoaded.value = true
    }
    Box(
        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
    ){
        //버튼이랑 플로팅 버튼이랑 여기에 넣어도 안 떠
        //val coroutineScopeMap = rememberCoroutineScope()

        if (!dataLoaded.value) {
            //스켈레톤 추가
            Box(modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Transparent)
            ){
                Text(
                    text = "지도 로드 중...",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            val viewPosition = averageLatLng(latLng)
//            cameraPositionState = CameraPositionState(position = CameraPosition.fromLatLngZoom(viewPosition, 15f))

            val zoomLevel = getZoomLevelForDistance(getMaxDistanceBetweenLatLng(viewPosition, latLng)) - 1
            val cameraPositionState = remember { CameraPositionState(position = CameraPosition.fromLatLngZoom(viewPosition, zoomLevel)) }

//            coroutineScopeMap.launch{
                LaunchedEffect(null){
                    coroutineScopeMap.launch{
                        syncedPhoto.forEach{
                            val cacheKey = "thumbnail_${it.id}"
                            val cachedBitmap = loadBitmapFromDiskCache(context, cacheKey)
                            if(cachedBitmap != null){
                                items.add(
                                    MyItem(
                                        LatLng(it.latitude, it.longitude),
                                        "PHOTO",
                                        "사진",
                                        cachedBitmap!!,
                                        "PHOTO",
                                        it.id
                                    )
                                )
                            }else{
                                val getResult = getThumbnailForPhoto(token!!, it.id)
                                items.add(
                                    MyItem(
                                        LatLng(it.latitude, it.longitude),
                                        "PHOTO",
                                        "사진",
                                        getResult!!,
                                        "PHOTO",
                                        it.id
                                    )
                                )
                                saveBitmapToDiskCache(context, getResult!!, cacheKey)
                            }
                    }
                }

                //사진 좌표와 비트맵
                latLngMarker.forEach{
                    items.add(MyItem(it,"LOCATION","위치", bitmap1, "POSITION", "HI"))
                }
                /*
                items.forEach {
                    if(it.itemType == "PHOTO"){
                        it.icon = getThumbnailForPhoto(token!!, it.id)!!
                    }
                }
                */
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
            ) {
//                var clusterManager by remember { mutableStateOf<ClusterManager<MyItem>?>(null) }
//                MapEffect(items) { map ->
//                    if (clusterManager == null) {
//                        clusterManager = ClusterManager<MyItem>(context, map)
//                    }
//                    clusterManager?.addItems(items)
//                    clusterManager?.renderer = MarkerClusterRender(context,map,clusterManager!!) {
//                    }
//                }
//                LaunchedEffect(key1 = cameraPositionState.isMoving) {
//                    if (!cameraPositionState.isMoving) {
//                        clusterManager?.onCameraIdle()
//                    }
//                }
                Clustering(
                    items = items,
                    // Optional: Handle clicks on clusters, cluster items, and cluster item info windows
                    onClusterClick = {
                        itemPopup = it.items.filter{it.itemType == "PHOTO"}
                        isPopupVisible = true
                        false
                    },
                    onClusterItemClick = {
                        if(it.itemType == "PHOTO"){
                            navHostController.navigate(CalendarStack.ClickDetailScreen.route+"/${it.id}/${date}") {
                                popUpTo(CalendarStack.ClickDetailScreen.route)
                            }
                        }
                        false
                    },
                    onClusterItemInfoWindowClick = {
                        false
                    },
                    // Optional: Custom rendering for clusters
                    clusterContent = { cluster ->
                        val size = 50.dp
                        val density = LocalDensity.current.density
                        val scaledSize = (size * density).toInt()
                        val scaledBitmap =
                            Bitmap.createScaledBitmap(bitmap1, scaledSize, scaledSize, false)!!
                                .asImageBitmap()
                        var scaledBitmap1 by remember { mutableStateOf<ImageBitmap?>(null) }

                        val clusterItems = cluster.items.toList()

                        // Check if there is a clusterItem with itemType "PHOTO"
                        val photoClusterItem = clusterItems.firstOrNull { it.itemType == "PHOTO" }

                        // Set the cluster icon based on the presence of a photoClusterItem
                        if (photoClusterItem != null) {
                            scaledBitmap1 = photoClusterItem.icon.let {
                                Bitmap.createScaledBitmap(it!!, scaledSize, scaledSize, false)
                            }!!.asImageBitmap()
                            Surface(
                                shape = RoundedCornerShape(percent = 10),
                                contentColor = Color.White,
                                border = BorderStroke(1.dp, Color.White),
                                elevation = 10.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Image(
                                        bitmap = scaledBitmap1!!,
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
                        } else {
                            Surface(
                                shape = CutCornerShape(12.dp),
                                color = Color.Transparent,
                                contentColor = Color.Red,
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_permission_location_foreground),
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp),
                                    tint = Color.Red
                                )
//                                Text(
//                                    "%,d".format(cluster.size), //이 부분 왜 2배로 나오지..?
//                                    fontSize = 16.sp,
//                                    fontWeight = FontWeight.Black,
//                                    textAlign = TextAlign.Center
//                                )
                            }
                        }
                    },
                    // Optional: Custom rendering for non-clustered items
                    clusterItemContent = { item ->
//                        val drawable = ContextCompat.getDrawable(context, R.drawable.img)
//                        val bitmap = com.lovestory.lovestory.ui.components.VectorToBitmap(
//                            vectorResId = R.drawable.ic_marker
//                        ).asImageBitmap()
//                        val size = 50.dp
//                        val scaledBitmap = item.icon.let {
//                            val density = LocalDensity.current.density
//                            val scaledSize = (size * density).toInt()
//                            Bitmap.createScaledBitmap(it, scaledSize, scaledSize, false)
//                        }!!.asImageBitmap()
//                        val size = 50.dp
//                        val density = LocalDensity.current.density
//                        val scaledSize = (size * density).toInt()
//                        val scaledSize2 = ((size/2) * density).toInt()
//                        val scaledBitmap = if(item.icon != bitmap1){
//                            Bitmap.createScaledBitmap(item.icon, scaledSize, scaledSize, false)!!.asImageBitmap()
//                        }else{
//                            Bitmap.createScaledBitmap(bitmap1, scaledSize2, scaledSize2, false)!!.asImageBitmap()
//                        }
                        val size = 50.dp
                        val density = LocalDensity.current.density
                        val scaledSize = (size * density).toInt()
                        if (item.itemType == "PHOTO") {
                            val scaledBitmap1 = item.icon.let{
                                Bitmap.createScaledBitmap(it!!, scaledSize, scaledSize, false)
                            }!!.asImageBitmap()
                            Surface(
                                shape = RoundedCornerShape(percent = 10),
                                contentColor = Color.White,
                                border = BorderStroke(1.dp, Color.White),
                                elevation = 10.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Image(
                                        bitmap = scaledBitmap1,
                                        contentDescription = null,
                                        modifier = Modifier.size(60.dp)
                                    )
                                }
                            }
                        } else {
                            Surface(
                                shape = CutCornerShape(12.dp),
                                color = Color.Transparent,
                                contentColor = Color.Red,
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_permission_location_foreground),
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp),
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                )
            }
        }

        FloatingActionButton(
            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
            onClick = {
                coroutineScopeMap.cancel()
                navHostController.popBackStack()
            },
            backgroundColor = colorResource(id = R.color.ls_pink)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                contentDescription = null,
                tint = Color.Black
            )
        }
    }

    if(isPopupVisible){
        MapDialog(
            onDismissRequest = { isPopupVisible = false },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        )
        {
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
//            val screenHeight = LocalConfiguration.current.screenHeightDp.dp
//            Column(
//                modifier = Modifier
//                    .width(screenWidth - 40.dp)
//                    .height(screenWidth)
//                    .clip(RoundedCornerShape(12.dp))
//                    .background(color = Color.White),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
            val boxWidth = remember { mutableStateOf(Dp.Unspecified) }
            val dens = LocalDensity.current
                Box(
                    modifier = Modifier
                        .width(screenWidth - 80.dp)
                        .height(screenWidth)
//                        .fillMaxWidth()
//                        .wrapContentHeight()
                        //.padding(20.dp)
                        .background(color = Color.White, RoundedCornerShape(12.dp))
                        .onSizeChanged {
                            boxWidth.value = it.width.toDp(dens)
                            Log.d("MapBoxWidth", "Width of the first Box: ${boxWidth.value}")
                        },
                    contentAlignment = Alignment.TopCenter
                ) {
                    val popupWidthDp = with(LocalDensity.current) {
                        LocalContext.current.resources.displayMetrics.widthPixels.dp
                    }
                    val filteredSyncedPhotos = syncedPhotosByDate
                        .filter { (date, photos) ->
                            photos.any { photo -> itemPopup.any { myItem -> myItem.id == photo.id } }
                        }

                    //isPopupVisibleSave = true
                    PhotoForMap(
                        syncedPhotosByDate = filteredSyncedPhotos,
                        token = token,
                        syncedPhotoView = syncedPhotoView,
                        navHostController = navHostController,
                        allPhotoListState = allPhotoListState,
                        widthDp = (boxWidth.value - 4.dp),
                        selectDate = date
                    )
                }
//            }
        }
    }
}


data class MyItem(
    val itemPosition: LatLng,
    val itemTitle: String,
    val itemSnippet: String,
    var icon: Bitmap,
    val itemType: String,
    val id: String
) : ClusterItem {
    override fun getPosition(): LatLng =
        itemPosition

    override fun getTitle(): String =
        itemTitle

    override fun getSnippet(): String =
        itemSnippet
}

//class MarkerClusterRender<T : MyItem>(
//    var context: Context,
//    var googleMap: GoogleMap,
//    clusterManager: ClusterManager<T>,
//    var onInfoWindowClick: (MyItem) -> Unit
//) :
//    DefaultClusterRenderer<T>(context, googleMap, clusterManager) {
//
//    private var clusterMap: HashMap<String, Marker> = hashMapOf()
//
//    override fun shouldRenderAsCluster(cluster: Cluster<T>): Boolean {
//        return cluster.size > 1
//    }
//
//    override fun getBucket(cluster: Cluster<T>): Int {
//        return cluster.size
//    }
//
//    override fun getClusterText(bucket: Int): String {
//        return super.getClusterText(bucket).replace("+", "")
//    }
//
//    override fun onBeforeClusterRendered(cluster: Cluster<T>, markerOptions: MarkerOptions) {
//        super.onBeforeClusterRendered(cluster, markerOptions)
//
//        val clusterItems = cluster.items.toList()
//
//        // Check if there is a clusterItem with itemType "PHOTO"
//        val photoClusterItem = clusterItems.find { it.itemType == "PHOTO" }
//
//        // Set the cluster icon based on the presence of a photoClusterItem
//        if (photoClusterItem != null) {
//            // Set the cluster icon as the icon of the first photoClusterItem
//            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(photoClusterItem.icon))
//        } else {
//            // Set the default cluster icon
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//            //markerOptions.icon(getDescriptorForCluster(cluster))
//        }
//    }
//
//    override fun onClustersChanged(clusters: Set<Cluster<T>>) {
//        super.onClustersChanged(clusters)
//
//        for (cluster in clusters) {
//            val clusterItems = cluster.items.toList()
//            val photoClusterItem = clusterItems.find { it.itemType == "PHOTO" }
//
//            for (clusterItem in clusterItems) {
//                val marker = getMarker(clusterItem)
//                if (marker != null) {
//                    if (photoClusterItem != null && clusterItem == photoClusterItem) {
//                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(photoClusterItem.icon))
//                    } else {
//                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//                    }
//                }
//            }
//        }
//    }
//
//    override fun onClusterItemRendered(clusterItem: T, marker: Marker) {
//        super.onClusterItemRendered(clusterItem, marker)
//        clusterMap[(clusterItem as MyItem).itemTitle] = marker
//
//        setMarker((clusterItem as MyItem), marker)
//    }
//
//    override fun onBeforeClusterItemRendered(item: T, markerOptions: MarkerOptions) {
//        super.onBeforeClusterItemRendered(item, markerOptions)
//
//        val myItem = item as MyItem
//
//        if(myItem.itemType == "PHOTO") {
//            val markerIcon = myItem.icon
//            val desiredSize = 60.dp // Set the desired size for the icon
//            val density = Resources.getSystem().displayMetrics.density
//            val scaledBitmap = Bitmap.createScaledBitmap(
//                markerIcon,
//                (desiredSize.value * density).toInt(),
//                (desiredSize.value * density).toInt(),
//                false
//            )
//            markerOptions.anchor(0.5f, 1f)
//            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))
//            markerOptions.title(myItem.itemTitle)
//            markerOptions.snippet(myItem.itemSnippet)
//        }else{
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//            markerOptions.title(myItem.itemTitle)
//            markerOptions.snippet(myItem.itemSnippet)
//        }
//
////        val myItem = item as MyItem
////        // Customize the markerOptions for individual items (not part of a cluster)
////        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
////        markerOptions.title(myItem.itemTitle)
////        markerOptions.snippet(myItem.itemSnippet)
//    }
//
//    private fun setMarker(poi: MyItem, marker: Marker?) {
//        val markerColor = BitmapDescriptorFactory.HUE_RED
////        marker?.let {
////            it.tag = poi
////            it.showInfoWindow()
////            changeMarkerColor(it, markerColor)
////        }
//        googleMap.setOnInfoWindowClickListener {
//            onInfoWindowClick(it.tag as MyItem)
//        }
//    }
//
//    private fun getClusterMarker(itemId: String): Marker? {
//        return if (clusterMap.containsKey(itemId)) clusterMap[itemId]
//        else null
//    }
//
//
//    fun showRouteInfoWindow(key: String) {
//        getClusterMarker(key)?.showInfoWindow()
//    }
//
//    private fun changeMarkerColor(marker: Marker, color: Float) {
//        try {
//            marker.setIcon(BitmapDescriptorFactory.defaultMarker(color));
//        } catch (ex: IllegalArgumentException) {
//            ex.printStackTrace()
//        } catch (ex: Exception) {
//            ex.printStackTrace()
//        }
//    }
//}

@Preview
@Composable
fun Preview(){
    val navController = rememberNavController()
    LoveStoryTheme() {
    }
}