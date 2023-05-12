package com.lovestory.lovestory.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.room.ExperimentalRoomApi
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lovestory.lovestory.R
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.entities.PhotoForSync
import com.lovestory.lovestory.database.entities.SyncedPhoto
import com.lovestory.lovestory.database.repository.PhotoForSyncRepository
import com.lovestory.lovestory.database.repository.SyncedPhotoRepository
import com.lovestory.lovestory.module.getImageById
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.module.loadBitmapFromDiskCache
import com.lovestory.lovestory.module.photo.getDetailPhoto
import com.lovestory.lovestory.module.saveBitmapToDiskCache
import com.lovestory.lovestory.view.SyncedPhotoView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.io.path.ExperimentalPathApi

@Composable
fun PhotoDetailScreenFromDevice(navHostController: NavHostController, photoId: String) {
    val systemUiController = rememberSystemUiController()

    val context = LocalContext.current

    val database = PhotoDatabase.getDatabase(context)
    val photoForSyncDao = database.photoForSyncDao()
    val repository = PhotoForSyncRepository(photoForSyncDao)

    val photoInfo = remember { mutableStateOf<PhotoForSync?>(null) }

    LaunchedEffect(photoId){
        photoInfo.value = repository.getPhotoForSyncById(photoId)!!
    }

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Black,
        )
    }

    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize(),
    ) {
        AnimatedVisibility(photoInfo.value != null, enter = fadeIn(), exit = fadeOut()){
            TransformableSample(imageUri = photoInfo.value!!.imageUrl!!)
        }
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
        ){
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0x2A000000))
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 20.dp)
            ){
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = null,
                    modifier = Modifier.clickable {navHostController.popBackStack() },
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(20.dp))
                AnimatedVisibility(photoInfo.value != null, enter = fadeIn(), exit = fadeOut()){
                    Column() {
                        val input = photoInfo.value!!.date
                        val formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")
                        val parsedDate = LocalDate.parse(input, formatter)

                        val outputFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)")
                        val output = parsedDate.format(outputFormatter)

                        Text(text = output, color = Color.White)
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PhotoDetailScreenFromServer(
    navHostController: NavHostController,
    syncedPhotoView: SyncedPhotoView,
    photoIndex : Int
){
    val syncedPhotos by syncedPhotoView.listOfSyncPhotos.observeAsState(initial = listOf())

    var isDropMenuForDetailPhoto by remember {mutableStateOf(false)}

    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()

    val pagerState = rememberPagerState()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Black,
        )
    }

    val database = PhotoDatabase.getDatabase(context)
    val syncedPhotoDao = database.syncedPhotoDao()
    val repository = SyncedPhotoRepository(syncedPhotoDao)

    val syncedPhoto by syncedPhotoView.syncedPhoto.observeAsState(null)

    val token = getToken(context)

    LaunchedEffect(null){
        if (photoIndex < 0){
            pagerState.scrollToPage(0)
        }
        else{
            pagerState.scrollToPage(photoIndex)
        }
    }

    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize(),
    ){

        HorizontalPager(
            count = syncedPhotos.size,
            state = pagerState,
            itemSpacing = 20.dp
        ) {index ->
            val cacheKey = "detail_${syncedPhotos[index].id}"
            val bitmap = remember { mutableStateOf<Bitmap?>(null) }

            LaunchedEffect(syncedPhotos[index].id) {
                val cachedBitmap = loadBitmapFromDiskCache(context, cacheKey)
                if (cachedBitmap != null) {
                    bitmap.value = cachedBitmap
                } else {
                    val getResult = getDetailPhoto(token!!, syncedPhotos[index].id, 20)
                    if(getResult != null){
                        saveBitmapToDiskCache(context, getResult, cacheKey)
                        bitmap.value = getResult
                    }
                    else{
                        Log.d("COMPONENT-detail photo", "Error in transfer bitmap")
                    }
                }
                Log.d("[Composable]PhotoDetailScreen", "start Detail ${syncedPhotos[index].id}")

                val photoInfo = repository.getSyncedPhotoById(syncedPhotos[index].id)
                if (photoInfo != null) {
                    syncedPhotoView.updateSyncedPhoto(photoInfo)
                }
            }
            AnimatedVisibility (bitmap.value != null,enter = fadeIn(), exit = fadeOut()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(Color.Black)
                        .fillMaxSize(),
                ){
                    DetailImageFromBitmap(bitmap.value!!)
                }
            }
        }


        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
        ){
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0x2A000000))
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 20.dp)
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = null,
                        modifier = Modifier.clickable {navHostController.popBackStack() },
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    AnimatedVisibility(syncedPhoto != null, enter = fadeIn(), exit = fadeOut()){
                        Column() {
                            val photoDate = LocalDate.parse(syncedPhotos[pagerState.currentPage]!!.date.substring(0, 10))
                            val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)", Locale.getDefault())
                            val formattedDate = photoDate.format(dateFormatter)

                            Text(text = syncedPhotos[pagerState.currentPage]!!.area1+" "+syncedPhotos[pagerState.currentPage]!!.area2+" "+syncedPhotos[pagerState.currentPage]!!.area3, color = Color.White)
                            Text(text = formattedDate, color = Color.White)
                        }
                    }

                }

                Box(){
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_more_vert_24),
                        contentDescription = null,
                        modifier = Modifier.clickable {isDropMenuForDetailPhoto = true},
                        tint = Color.White
                    )
                    DropdownMenu(
                        expanded = isDropMenuForDetailPhoto,
                        onDismissRequest = { isDropMenuForDetailPhoto = false },
                        modifier = Modifier.wrapContentSize()
                    ) {
                        DropdownMenuItem(onClick = {
                            isDropMenuForDetailPhoto= false
                            CoroutineScope(Dispatchers.IO).launch {
                                if (token != null) {
                                    getImageById(context = context, token = token, photo_id = syncedPhotos[pagerState.currentPage]!!.id)
                                }
                            }

                        }) {
                            Text(text = "원본 사진 다운로드")
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun DetailImageFromBitmap(bitmap: Bitmap){
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
//    val imageWidth = screenWidth / 3 - 10.dp

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth(),
        contentScale = ContentScale.FillWidth
    )
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TransformableSample(imageUri: String) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, _, _ ->
        val newScale = scale * zoomChange
        if (newScale in 1f..3f) {
            scale = newScale
        }

    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize(),
    ){
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(data = imageUri)
                    .build()
            ),
            contentDescription = null,
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        val newOffsetX = offset.x + dragAmount.x
                        val newOffsetY = offset.y + dragAmount.y
                        val maxXOffset: Float =
                            if (scale > 1) (screenWidth * scale - screenWidth) / 2.dp else 0F
                        val maxYOffset: Float =
                            if (scale > 1) (screenWidth * scale - screenWidth) / 2.dp else 0F

                        if (newOffsetX in -maxXOffset..maxXOffset) {
                            offset = Offset(newOffsetX, offset.y)
                        }
                        if (newOffsetY in -maxYOffset..maxYOffset) {
                            offset = Offset(offset.x, newOffsetY)
                        }
                    }
                }
                .transformable(state = state)
                .background(Color.Black)
                .width(screenWidth),
            contentScale = ContentScale.Fit
        )
    }

}
