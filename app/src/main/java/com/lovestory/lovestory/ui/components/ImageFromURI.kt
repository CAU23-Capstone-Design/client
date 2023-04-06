package com.lovestory.lovestory.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest


@Composable
fun DisplayImageFromUri(index : Int,    checked : Boolean, imageUri: String, onChangeChecked : (Int)->Unit) {
    val borderColor = if (checked) Color.Blue else Color.Transparent

    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest
                .Builder(LocalContext.current)
                .data(data = imageUri)
                .build()
        ),
        contentDescription = null,
        modifier = Modifier
            .height(100.dp)
            .width(100.dp)
            .padding(5.dp)
            .border(width = 2.dp, color = borderColor)
            .clickable { onChangeChecked(index) },
        contentScale = ContentScale.Crop
    )
}