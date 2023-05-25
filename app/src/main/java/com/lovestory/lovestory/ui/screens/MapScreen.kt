package com.lovestory.lovestory.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.lovestory.lovestory.R
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.entities.SyncedPhoto
import com.lovestory.lovestory.database.repository.SyncedPhotoRepository
import com.lovestory.lovestory.graphs.CalendarStack
import com.lovestory.lovestory.model.*
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.module.loadBitmapFromDiskCache
import com.lovestory.lovestory.module.photo.getThumbnailForPhoto
import com.lovestory.lovestory.module.saveBitmapToDiskCache
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
    val items_google = remember{ mutableStateListOf<MyItem>() }

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
                            }
                        }
                    },
                    // Optional: Custom rendering for non-clustered items
                    clusterItemContent = { item ->
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
//                            Log.d("MapBoxWidth", "Width of the first Box: ${boxWidth.value}")
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

@Preview
@Composable
fun Preview(){
    val navController = rememberNavController()
    LoveStoryTheme() {
    }
}