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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lovestory.lovestory.module.loadBitmapFromDiskCache
import com.lovestory.lovestory.module.photo.getDetailPhoto
import com.lovestory.lovestory.module.saveBitmapToDiskCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

@Composable
fun BigThumbnailFromServer(
    index: Int,
    token: String,
    photoId: String,
    location : String,
    allPhotoListState : LazyListState,
    setSelectedButton : (String)->Unit,
    curIndex : Int
){
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    val cacheKey = "detail_${photoId}"

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
        BigThumbnail(
            index= index,
            bitmap = bitmap.value!!,
            photoId = photoId,
            location = location,
            allPhotoListState= allPhotoListState,
            setSelectedButton = setSelectedButton,
            curIndex = curIndex
        )
    }
    AnimatedVisibility (bitmap.value== null, enter = fadeIn(), exit = fadeOut()){
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
//        val imageWidth = screenWidth

        Skeleton(modifier = Modifier
            .width(screenWidth)
            .height(screenWidth / 2)
            .padding(2.dp)
            .clip(RoundedCornerShape(10.dp)),
        )
    }
}

@Composable
fun BigThumbnail(
    index: Int,
    bitmap: Bitmap,
    photoId: String,
    location: String,
    allPhotoListState: LazyListState,
    setSelectedButton : (String)->Unit,
    curIndex : Int
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
//    val imageWidth = screenWidth - 5.dp

    Box(
        modifier = Modifier
            .width(screenWidth)
            .height(screenWidth / 2)
            .padding(2.dp)
            .clip(RoundedCornerShape(10.dp)),
    ){
        val gradient = Brush.linearGradient(
            colors = listOf(
                Color(0x9F000000),
                Color(0x0)
            )
        )
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .width(screenWidth)
                .height(screenWidth / 2)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                    setSelectedButton("전체")
                    CoroutineScope(Dispatchers.Main).launch {
                        allPhotoListState.scrollToItem(index = curIndex-1)
                    }
                }
            ,
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .width(screenWidth)
                .height(screenWidth / 2)
                .clip(RoundedCornerShape(10.dp)),
        ){
            Row() {
                Text(
                    text = location,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 15.dp, top = 15.dp)
                )
            }
        }
    }
}