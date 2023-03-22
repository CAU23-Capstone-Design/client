package com.lovestory.lovestory.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lovestory.lovestory.R

@Composable
fun Avatar(imgUrl : String?, sizeAvatar : Int){
    Image(
        painter = painterResource(id = R.drawable.thumbnail_default),
        contentDescription = "overlay_thumbnail",
        modifier = Modifier
            .size(sizeAvatar.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
    )
}