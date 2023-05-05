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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lovestory.lovestory.module.loadBitmapFromDiskCache
import com.lovestory.lovestory.module.photo.getDetailPhoto
import com.lovestory.lovestory.module.saveBitmapToDiskCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun BigThumbnailFromServer(
    index: Int,
    token: String,
    photoId: String,
    allPhotoListState : LazyListState,
    setSelectedButton : (String)->Unit
){
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    val cacheKey = "big_thumbnail_$photoId"

    LaunchedEffect(key1 = photoId){
        val cachedBitmap = loadBitmapFromDiskCache(context, cacheKey)
        if(cachedBitmap != null){
            bitmap.value = cachedBitmap
        }
        else{
            val getResult = getDetailPhoto(token!!, photoId, 20)
            if(getResult != null){
                saveBitmapToDiskCache(context, getResult, cacheKey)
                bitmap.value = getResult
            }
            else{
                Log.d("COMPONENT-BigThumbnailFromServer", "Error in transfer bitmap")
            }
        }
    }

    AnimatedVisibility (bitmap.value!= null,enter = fadeIn(), exit = fadeOut()){
        BigThumbnail(index, bitmap.value!!, photoId, allPhotoListState= allPhotoListState, setSelectedButton = setSelectedButton)
    }
    AnimatedVisibility (bitmap.value== null, enter = fadeIn(), exit = fadeOut()){
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
//        val imageWidth = screenWidth

        Skeleton(modifier = Modifier
            .width(screenWidth).height(screenWidth/2)
            .padding(2.dp).clip(RoundedCornerShape(10.dp)),
        )
    }
}

@Composable
fun BigThumbnail(
    index: Int,
    bitmap: Bitmap,
    photoId: String,
    allPhotoListState: LazyListState,
    setSelectedButton : (String)->Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
//    val imageWidth = screenWidth - 5.dp

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        modifier = Modifier
            .width(screenWidth).height(screenWidth/2)
            .padding(2.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                setSelectedButton("전체")
                CoroutineScope(Dispatchers.Main).launch {
                    allPhotoListState.scrollToItem(index =9)
                }
            }
        ,
        contentScale = ContentScale.Crop,
    )
}