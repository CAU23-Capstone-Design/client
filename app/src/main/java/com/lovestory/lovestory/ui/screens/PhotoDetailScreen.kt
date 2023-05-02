package com.lovestory.lovestory.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.lang.Float.max
import java.lang.Float.min

@Composable
fun PhotoDetailScreen(navController: NavController, imageUri: String) {
//    val image = imageResource(id = imageResId).asImageBitmap()

//    Surface {
//        Box(modifier = Modifier.fillMaxSize()) {
//            ZoomableImage(
//                image = image,
//                modifier = Modifier.fillMaxSize()
//            )
//        }
//    }
    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
    ) {
        TransformableSample(imageUri = imageUri)
    }
}

//@Composable
//fun ZoomableImage(image: ImageBitmap, modifier: Modifier = Modifier) {
//    var scale by remember { mutableStateOf(1f) }
//    var translation by remember { mutableStateOf(Offset.Zero) }
//
//    val gestureModifier = Modifier.pointerInput(Unit) {
//        detectTransformGestures { _, _, zoom, _ ->
//            scale *= zoom
//            scale = max(0.5f, min(scale, 4f)) // 최소 및 최대 확대/축소 범위를 제한
//        }
//    }
//
//    Canvas(modifier = modifier.then(gestureModifier)) {
//        translate(left = translation.x, top = translation.y) {
//            scale(scale = scale) {
//                drawImage(image)
//            }
//        }
//    }
//}

@Composable
fun TransformableSample(imageUri: String) {
    // set up all transformation states
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        val newScale = scale * zoomChange
        if (newScale in 0.5f..4f) { // 확대/축소 범위를 0.5배에서 4배로 제한
            scale = newScale
        }
        rotation += rotationChange

        // Box와 이미지의 크기를 고려하여 새로운 오프셋 계산
        val newOffsetX = offset.x + offsetChange.x
        val newOffsetY = offset.y + offsetChange.y
        val maxXOffset = (screenWidth * scale - screenWidth) / 2.dp
        val maxYOffset = (screenWidth * scale - screenWidth) / 2.dp

        // 화면 밖으로 벗어나지 않도록 오프셋 제한
        if (newOffsetX in -maxXOffset..maxXOffset) {
            offset = Offset(newOffsetX, offset.y)
        }
        if (newOffsetY in -maxYOffset..maxYOffset) {
            offset = Offset(offset.x, newOffsetY)
        }
    }
    Box(
        Modifier
            // apply other transformations like rotation and zoom
            // on the pizza slice emoji
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
            // add transformable to listen to multitouch transformation events
            // after offset
            .transformable(state = state)
            .background(Color.White)
            .fillMaxSize()
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
                .width(screenWidth),
//                .border(width = 2.dp, color = borderColor)
//                .clickable { onChangeChecked(index) },
            contentScale = ContentScale.Fit
        )
    }
}