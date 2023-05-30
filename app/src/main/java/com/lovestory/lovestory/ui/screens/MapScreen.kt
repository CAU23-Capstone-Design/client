package com.lovestory.lovestory.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import androidx.navigation.NavHostController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.compose.*
import com.lovestory.lovestory.R
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.entities.SyncedPhoto
import com.lovestory.lovestory.database.repository.SyncedPhotoRepository
import com.lovestory.lovestory.graphs.CalendarStack
import com.lovestory.lovestory.model.*
import com.lovestory.lovestory.module.*
import com.lovestory.lovestory.module.photo.getThumbnailForPhoto
import com.lovestory.lovestory.network.getGps
import com.lovestory.lovestory.ui.components.*
import com.lovestory.lovestory.view.SyncedPhotoView
import kotlinx.coroutines.cancel
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

    lateinit var repository : SyncedPhotoRepository
    var photoDate by remember { mutableStateOf(emptyList<String>()) }
    var photoPosition by remember { mutableStateOf(emptyList<LatLng>()) }

    var bitmapList by remember { mutableStateOf(emptyList<Bitmap>()) }

//    val drawable1 = ContextCompat.getDrawable(context, R.drawable.img)
//    val bitmap1 = (drawable1 as BitmapDrawable).bitmap/**/

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

        photoPosition.forEach {
            latLng += it
        }

        dataLoaded.value = true
    }
    Box(
        modifier = Modifier.fillMaxSize(),
    ){

        if (!dataLoaded.value) {
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

            val zoomLevel = getZoomLevelForDistance(getMaxDistanceBetweenLatLng(viewPosition, latLng)) - 1
            val cameraPositionState = remember { CameraPositionState(position = CameraPosition.fromLatLngZoom(viewPosition, zoomLevel)) }

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
                    items.add(MyItem(it,"LOCATION","${it.latitude}, ${it.longitude}", null, "POSITION", "HI"))
                }
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
            ) {
                var clusterManager by remember { mutableStateOf<ClusterManager<MyItem>?>(null) }
                MapEffect(items) { map ->
                    if (clusterManager == null) {
                        clusterManager = ClusterManager<MyItem>(context, map)
                    }
                    clusterManager?.addItems(items)
                    clusterManager?.renderer = MarkerClusterRender(context,map,clusterManager!!) {
                    }
                    clusterManager?.setOnClusterClickListener {
                        itemPopup = it.items.filter{it.itemType == "PHOTO"}
                        if(itemPopup.isNotEmpty()){
                            isPopupVisible = true
                        }
                        false
                    }
                    clusterManager?.setOnClusterItemClickListener {
                        if(it.itemType == "PHOTO"){
                            navHostController.navigate(CalendarStack.ClickDetailScreen.route+"/${it.id}/${date}") {
                                popUpTo(CalendarStack.ClickDetailScreen.route)
                            }
                        }
                        false
                    }
                }
                LaunchedEffect(key1 = cameraPositionState.isMoving) {
                    if (!cameraPositionState.isMoving) {
                        clusterManager?.onCameraIdle()
                    }
                }
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
        LoveStoryDialog(
            onDismissRequest = {
                isPopupVisible = false
                itemPopup = emptyList()
            },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        )
        {
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val boxWidth = remember { mutableStateOf(Dp.Unspecified) }
            val dens = LocalDensity.current
                Box(
                    modifier = Modifier
                        .width(screenWidth - 80.dp)
                        .height(screenWidth)
                        .background(color = Color.White, RoundedCornerShape(12.dp))
                        .onSizeChanged {
                            boxWidth.value = it.width.toDp(dens)
                        },
                    contentAlignment = Alignment.TopCenter
                ) {
                    val popupWidthDp = with(LocalDensity.current) {
                        LocalContext.current.resources.displayMetrics.widthPixels.dp
                    }
                    val filteredSyncedPhotos = syncedPhotosByDate.mapValues { (_, photos) ->
                        photos.filter { synced ->
                            itemPopup.any { item ->
                                synced.id == item.id
                            }
                        }
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
        }
    }
}


data class MyItem(
    val itemPosition: LatLng,
    val itemTitle: String,
    val itemSnippet: String,
    var icon: Bitmap?,
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

class MarkerClusterRender<T : MyItem>(
    var context: Context,
    var googleMap: GoogleMap,
    clusterManager: ClusterManager<T>,
    var onInfoWindowClick: (MyItem) -> Unit,
) :
    DefaultClusterRenderer<T>(context, googleMap, clusterManager) {

    private var clusterMap: HashMap<String, Marker> = hashMapOf()

    override fun shouldRenderAsCluster(cluster: Cluster<T>): Boolean {
        return cluster.size > 1
    }

    override fun getBucket(cluster: Cluster<T>): Int {
        cluster.items.removeAll { it.itemType == "POSITION" }
        return cluster.size
    }

    override fun getClusterText(bucket: Int): String {
        return super.getClusterText(bucket).replace("+", "")
    }

    override fun setOnClusterClickListener(listener: ClusterManager.OnClusterClickListener<T>?) {
        super.setOnClusterClickListener(listener)
    }

    override fun onBeforeClusterRendered(cluster: Cluster<T>, markerOptions: MarkerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions)

        val clusterItems = cluster.items.toList()

        // Check if there is a clusterItem with itemType "PHOTO"
        val photoClusterItem = clusterItems.find { it.itemType == "PHOTO" }

        // Set the cluster icon based on the presence of a photoClusterItem
        if (photoClusterItem != null) {
            // Set the cluster icon as the icon of the first photoClusterItem
            markerOptions.icon(clusterIcon(context, photoClusterItem.icon!!, getBucket(cluster)))//BitmapDescriptorFactory.fromBitmap(photoClusterItem.icon))
        } else {
            // Set the default cluster icon
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            //markerOptions.icon(getDescriptorForCluster(cluster))
        }
    }

    override fun onClusterUpdated(cluster: Cluster<T>, marker: Marker) {
        super.onClusterUpdated(cluster, marker)

        val clusterItems = cluster.items.toList()

        // Check if there is a clusterItem with itemType "PHOTO"
        val photoClusterItem = clusterItems.find { it.itemType == "PHOTO" }

        // Set the cluster icon based on the presence of a photoClusterItem
        if (photoClusterItem != null) {
            // Set the cluster icon as the icon of the first photoClusterItem
            marker.setIcon(clusterIcon(context, photoClusterItem.icon!!, getBucket(cluster)))
        } else {
            // Set the default cluster icon
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            //markerOptions.icon(getDescriptorForCluster(cluster))
        }
    }

    override fun onClustersChanged(clusters: Set<Cluster<T>>) {
        super.onClustersChanged(clusters)

        for (cluster in clusters) {
            val clusterItems = cluster.items.toList()
            val photoClusterItem = clusterItems.find { it.itemType == "PHOTO" }

            for (clusterItem in clusterItems) {
                val marker = getMarker(clusterItem)
                if (marker != null) {
                    if (photoClusterItem != null && clusterItem == photoClusterItem) {
                        marker.setIcon(clusterIcon(context, photoClusterItem.icon!!, getBucket(cluster)))
                    } else {
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    }
                }
            }
        }
    }

    override fun onClusterItemRendered(clusterItem: T, marker: Marker) {
        super.onClusterItemRendered(clusterItem, marker)
        clusterMap[(clusterItem as MyItem).itemTitle] = marker

        //setMarker((clusterItem as MyItem), marker)
    }

    override fun onBeforeClusterItemRendered(item: T, markerOptions: MarkerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions)

        val myItem = item as MyItem

        if(myItem.itemType == "PHOTO") {
            val markerIcon = myItem.icon
            val desiredSize = 60.dp // Set the desired size for the icon
            val density = Resources.getSystem().displayMetrics.density
            val scaledBitmap = Bitmap.createScaledBitmap(
                markerIcon!!,
                (desiredSize.value * density).toInt(),
                (desiredSize.value * density).toInt(),
                false
            )
            markerOptions.anchor(0.5f, 1f)
            markerOptions.icon(markerIcon(context, myItem.icon!!))
            markerOptions.title(myItem.itemTitle)
            markerOptions.snippet(myItem.itemSnippet)
        }else{
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            markerOptions.title(myItem.itemTitle)
            markerOptions.snippet(myItem.itemSnippet)
        }
    }

    private fun clusterIcon(context: Context, bitmap : Bitmap, size: Int): BitmapDescriptor {
        val clusterView = ClusterView(context, bitmap, size)
        return BitmapDescriptorFactory.fromBitmap(clusterView.toBitmap(50.dpToPx(context), 50.dpToPx(context)))
    }

    private fun markerIcon(context: Context, bitmap : Bitmap): BitmapDescriptor {
        // Customize your ClusterView. The cluster gives you its size (cluster.size) and its items within it (cluster.items)
        // Use that to customize the cluster appearance.
        val markerView = MarkerView(context, bitmap)
        return BitmapDescriptorFactory.fromBitmap(markerView.toBitmap(50.dpToPx(context), 50.dpToPx(context)))
    }
}