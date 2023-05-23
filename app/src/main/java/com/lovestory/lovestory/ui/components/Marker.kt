package com.lovestory.lovestory.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

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

@Composable
public fun Dp.toInt(): Int {
    return (this.value * LocalDensity.current.density).toInt()
}
