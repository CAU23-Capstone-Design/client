package com.lovestory.lovestory.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lovestory.lovestory.R

@Composable
fun SelectAvatar(imgUrl : String, sizeAvatar : Int) {
    Box(
        modifier = Modifier.size(sizeAvatar.dp),
        contentAlignment = Alignment.Center
    ){
        Image(
            painter = painterResource(id = R.drawable.thumbnail_default),
            contentDescription = "thumbnail_default",
            modifier = Modifier
                .size(sizeAvatar.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Avatar(imgUrl = imgUrl, sizeAvatar = sizeAvatar)
    }
}