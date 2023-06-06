package com.lovestory.lovestory.ui.components

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size

@Composable
fun GifImage(
    modifier: Modifier = Modifier,
    assetName : String,
) {
    val context = LocalContext.current
    val tempFile = copyAssetToTempFile(context, assetName)
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Box(modifier = Modifier
        .height(250.dp)
        .width(250.dp)){
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context).data(data = tempFile).apply(block = {
                    size(Size.ORIGINAL)
                }).build(), imageLoader = imageLoader
            ),
            contentDescription = null,
            contentScale = androidx.compose.ui.layout.ContentScale.FillWidth,
            modifier = modifier.fillMaxWidth(),
        )
    }

}