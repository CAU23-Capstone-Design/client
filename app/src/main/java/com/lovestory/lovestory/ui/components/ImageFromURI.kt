package com.lovestory.lovestory.ui.components

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.lovestory.lovestory.R
import com.lovestory.lovestory.database.entities.PhotoForSync
import com.lovestory.lovestory.database.entities.SyncedPhoto
import com.lovestory.lovestory.graphs.GalleryStack
import com.lovestory.lovestory.graphs.MainScreens
import com.lovestory.lovestory.module.loadBitmapFromDiskCache
import com.lovestory.lovestory.module.photo.getThumbnailForPhoto
import com.lovestory.lovestory.module.saveBitmapToDiskCache
import com.lovestory.lovestory.network.getThumbnailById
import com.lovestory.lovestory.view.SyncedPhotoView
import com.squareup.moshi.Moshi

@Composable
fun Skeleton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.LightGray)
            .animateContentSize()
    )
}

@Composable
fun ThumbnailOfPhotoFromServer(
    index: Int,
    token: String,
    photo: SyncedPhoto,
    navHostController: NavHostController,
    syncedPhotoView : SyncedPhotoView,
    isPressedPhotoMode : MutableState<Boolean>
) {
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val indexForDetail = remember { mutableStateOf(0) }
//    val cacheKey = "lovestory_ca"+photoId
//    lateinit var bitmapOfThumbnail : Bitmap
    val cacheKey = "thumbnail_${photo.id}"

    LaunchedEffect(photo) {
        indexForDetail.value = syncedPhotoView.getAllSyncedPhotoIndex(photo)
        val cachedBitmap = loadBitmapFromDiskCache(context, cacheKey)
        if (cachedBitmap != null) {
//            Log.d("Thumbnail","cache에서 로드")
            bitmap.value = cachedBitmap
        } else {
            val getResult = getThumbnailForPhoto(token, photo.id)
            if(getResult != null){
                saveBitmapToDiskCache(context, getResult, cacheKey)
                bitmap.value = getResult
//                Log.d("Thumbnail","서버에서 로드")
            }
            else{
                Log.d("COMPONENT-ThumbnailOfPhotoFromServer", "Error in transfer bitmap")
            }
        }
    }

    AnimatedVisibility (bitmap.value != null, enter = fadeIn(), exit = fadeOut()) {
        DisplayImageFromBitmap(index, bitmap.value!!, navHostController=navHostController, photoId = photo.id, photoIndex = indexForDetail, isPressedPhotoMode = isPressedPhotoMode)
    }
    AnimatedVisibility(bitmap.value== null, enter = fadeIn(), exit = fadeOut()) {
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val imageWidth = (screenWidth / 3)
//        Log.d("image width", "$imageWidth")
        Skeleton(modifier = Modifier
            .width(imageWidth)
            .aspectRatio(1f)
            .padding(2.dp))
    }
}

@Composable
fun DisplayImageFromBitmap(index: Int, bitmap: Bitmap, navHostController: NavHostController, photoId: String, photoIndex : MutableState<Int>, isPressedPhotoMode : MutableState<Boolean>) {
    val checked = remember {
        mutableStateOf(false)
    }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val imageWidth = screenWidth / 3

    val haptic = LocalHapticFeedback.current

    Box{
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .width(imageWidth)
                .aspectRatio(1f)
                .padding(2.dp)
                .clickable {
                    navHostController.navigate(GalleryStack.DetailPhotoFromServer.route + "/${photoIndex.value}") {
                        popUpTo(GalleryStack.PhotoSync.route)
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(onLongPress = {
                        isPressedPhotoMode.value = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    })
                },
            contentScale = ContentScale.Crop,
        )
        AnimatedVisibility(visible = isPressedPhotoMode.value, enter = fadeIn(), exit = fadeOut()) {
            AnimatedVisibility(checked.value, enter = fadeIn(), exit = fadeOut()){
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0x88DFA8A8))
                        .width(imageWidth)
                        .aspectRatio(1f)
                        .padding(2.dp),
//                .border(width = 2.dp, color = borderColor)
                ){}
            }
            AnimatedVisibility(checked.value, enter = fadeIn(), exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 5.dp, top = 5.dp))
            {
                Box(modifier = Modifier
                    .width(35.dp)
                    .height(35.dp)
                    .clickable {checked.value = false },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_check_circle_24),
                        contentDescription = null,
                        tint = Color(0xFFF8B0B0),
                    )
                }
            }
            AnimatedVisibility(!checked.value, enter = fadeIn(), exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 5.dp, top = 5.dp))
            {
                Box(modifier = Modifier
                    .width(35.dp)
                    .height(35.dp)
                    .clickable { checked.value = true },
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_check_circle_outline_24),
                        contentDescription = null,
                        tint = Color(0xFF6B6B6B),
                        modifier = Modifier
                    )
                }

            }
        }

    }
}

@Composable
fun CheckableDisplayImageFromUri(navHostController :NavHostController,index : Int, checked : Boolean, imageInfo: PhotoForSync, onChangeChecked : (Int)->Unit) {
    val borderColor = if (checked) Color(0xFFEEC9C9) else Color.Transparent
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val imageWidth = screenWidth / 3 - 4.dp

    Box{
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(data = imageInfo.imageUrl)
                    .build()
            ),
            contentDescription = null,
            modifier = Modifier
                .width(imageWidth)
                .aspectRatio(1f)
                .padding(2.dp)
//                .border(width = 2.dp, color = borderColor)
                .clickable {
                    navHostController.navigate(GalleryStack.DetailPhotoFromDevice.route + "/${imageInfo.id}") {
                        popUpTo(GalleryStack.PhotoSync.route)
                    }
                },
            contentScale = ContentScale.Crop
        )

        AnimatedVisibility(checked, enter = fadeIn(), exit = fadeOut()){
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0x88DFA8A8))
                    .width(imageWidth)
                    .aspectRatio(1f)
                    .padding(2.dp),
//                .border(width = 2.dp, color = borderColor)
            ){}
        }
        AnimatedVisibility(checked, enter = fadeIn(), exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 5.dp, top = 5.dp))
        {
            Box(modifier = Modifier
                .width(35.dp)
                .height(35.dp)
                .clickable { onChangeChecked(index) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_check_circle_24),
                    contentDescription = null,
                    tint = Color(0xFFF8B0B0),
                )
            }
        }
        AnimatedVisibility(!checked, enter = fadeIn(), exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 5.dp, top = 5.dp))
        {
            Box(modifier = Modifier
                .width(35.dp)
                .height(35.dp)
                .clickable { onChangeChecked(index) },
                contentAlignment = Alignment.Center
            ){
                Icon(
                    painter = painterResource(id = R.drawable.baseline_check_circle_outline_24),
                    contentDescription = null,
                    tint = Color(0xFF6B6B6B),
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
fun DisplayImageFromUri(index : Int, imageUri: String) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val imageWidth = screenWidth / 3 - 10.dp

    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest
                .Builder(LocalContext.current)
                .data(data = imageUri)
                .build()
        ),
        contentDescription = null,
        modifier = Modifier
//            .height(imageWidth)
            .width(imageWidth)
            .aspectRatio(1f)
            .padding(2.dp)
//            .clip(RoundedCornerShape(10.dp))
        ,
        contentScale = ContentScale.Crop
    )
}