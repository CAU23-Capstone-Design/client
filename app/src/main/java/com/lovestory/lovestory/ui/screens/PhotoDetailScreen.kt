package com.lovestory.lovestory.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import com.lovestory.lovestory.R

@Composable
fun PhotoDetailScreen(navHostController: NavHostController, imageUri: String) {
    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize(),
    ) {
        TransformableSample(imageUri = imageUri)

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
                    .height(48.dp)
                    .padding(horizontal = 20.dp)
            ){
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = null,
                    modifier = Modifier.clickable {navHostController.popBackStack() },
                    tint = Color.White
                )
            }
        }

    }
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
//        rotation += rotationChange
//        rotation += rotationChange

//        val newOffsetX = offset.x + offsetChange.x
//        val newOffsetY = offset.y + offsetChange.y
//        val maxXOffset = (screenWidth * scale - screenWidth) / 2.dp
//        val maxYOffset = (screenWidth * scale - screenWidth) / 2.dp
//
//        if (newOffsetX in -maxXOffset..maxXOffset) {
//            offset = Offset(newOffsetX, offset.y)
//        }
//        if (newOffsetY in -maxYOffset..maxYOffset) {
//            offset = Offset(offset.x, newOffsetY)
//        }
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
                        val maxXOffset : Float =
                            if (scale > 1) (screenWidth * scale - screenWidth) / 2.dp else 0F
                        val maxYOffset : Float =
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
