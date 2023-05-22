package com.lovestory.lovestory.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

/*
@Composable
fun CustomMarker(
    imageUri: Uri,
    size: Dp = 48.dp
): Painter {
    val context = LocalContext.current
    val inputStream = context.contentResolver.openInputStream(imageUri)
    val imageBitmap = BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
        ?: throw IllegalArgumentException("Failed to load image from URI: $imageUri")

    return BitmapPainter(imageBitmap)
}
*/


@Composable
fun CustomMarker(uri: Uri, size: Dp = 30.dp): BitmapDescriptor {
    val context = LocalContext.current
    val bitmap = context.contentResolver.openFileDescriptor(uri, "r")?.use { descriptor ->
        BitmapFactory.decodeFileDescriptor(descriptor.fileDescriptor)
    }
    val scaledBitmap = bitmap?.let {
        val density = LocalDensity.current.density
        val scaledSize = (size * density).toInt()
        Bitmap.createScaledBitmap(it, scaledSize, scaledSize, false)
    }
    //Canvas(bitma)
    return scaledBitmap?.let { BitmapDescriptorFactory.fromBitmap(it) } ?: BitmapDescriptorFactory.defaultMarker()
}

/*
@Composable'
fun CustomMarker(uri: Uri, size: Dp = 48.dp): BitmapDescriptor {
    val context = LocalContext.current
    val bitmap = context.contentResolver.openFileDescriptor(uri, "r")?.use { descriptor ->
        BitmapFactory.decodeFileDescriptor(descriptor.fileDescriptor)
    }
    val scaledBitmap = bitmap?.let {
        val density = LocalDensity.current.density
        val scaledSize = (size * density).toInt()
        Bitmap.createScaledBitmap(it, scaledSize, scaledSize, false)
    }

    val fixedSizeBitmap = scaledBitmap?.let {
        val density = LocalDensity.current.density
        val scaledSize = (size * density).toInt()
        Bitmap.createBitmap(scaledSize, scaledSize, Bitmap.Config.ARGB_8888)
            .also { canvasBitmap ->
                Canvas(canvasBitmap).drawBitmap(scaledBitmap, 0f, 0f, null)
            }
    }
    return fixedSizeBitmap?.let { BitmapDescriptorFactory.fromBitmap(it) } ?: BitmapDescriptorFactory.defaultMarker()
}

 */

@Composable
public fun Dp.toInt(): Int {
    return (this.value * LocalDensity.current.density).toInt()
}
