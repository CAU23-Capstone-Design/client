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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lovestory.lovestory.R
import com.lovestory.lovestory.database.PhotoDatabase
import com.lovestory.lovestory.database.entities.PhotoForSync
import com.lovestory.lovestory.database.entities.SyncedPhoto
import com.lovestory.lovestory.database.repository.PhotoForSyncRepository
import com.lovestory.lovestory.database.repository.SyncedPhotoRepository
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.module.loadBitmapFromDiskCache
import com.lovestory.lovestory.module.photo.getDetailPhoto
import com.lovestory.lovestory.module.saveBitmapToDiskCache
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

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

@Composable
fun PhotoDetailScreenFromServer(navHostController: NavHostController, photoId : String){
    var isDropMenuForDetailPhoto by remember {mutableStateOf(false)}

    val context = LocalContext.current

    val database = PhotoDatabase.getDatabase(context)
    val syncedPhotoDao = database.syncedPhotoDao()
    val repository = SyncedPhotoRepository(syncedPhotoDao)

    val syncedPhoto = remember { mutableStateOf<SyncedPhoto?>(null) }

    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    val cacheKey = "detail_${photoId}"

    val token = getToken(context)

    val systemUiController = rememberSystemUiController()

    LaunchedEffect(photoId) {
        val cachedBitmap = loadBitmapFromDiskCache(context, cacheKey)
        if (cachedBitmap != null) {
            Log.d("detail","cache에서 로드")
            bitmap.value = cachedBitmap
        } else {
            val getResult = getDetailPhoto(token!!, photoId, 20)
            if(getResult != null){
                saveBitmapToDiskCache(context, getResult, cacheKey)
                bitmap.value = getResult
                Log.d("detail","서버에서 로드")
            }
            else{
                Log.d("COMPONENT-detail photo", "Error in transfer bitmap")
            }
        }

        val photoInfo = repository.getSyncedPhotoById(photoId)
        syncedPhoto.value = photoInfo
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
    ){
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
                    AnimatedVisibility(syncedPhoto.value != null, enter = fadeIn(), exit = fadeOut()){
                        Column() {
                            val photoDate = LocalDate.parse(syncedPhoto.value!!.date.substring(0, 10))
                            val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)", Locale.getDefault())
                            val formattedDate = photoDate.format(dateFormatter)

                            Text(text = syncedPhoto.value!!.area1+" "+syncedPhoto.value!!.area2+" "+syncedPhoto.value!!.area3, color = Color.White)
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
@OptIn(ExperimentalMaterial3Api::class)
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
//                modifier = Modifier
//                    .width(screenWidth),
//                .border(width = 2.dp, color = borderColor)
//                .clickable { onChangeChecked(index) },
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
