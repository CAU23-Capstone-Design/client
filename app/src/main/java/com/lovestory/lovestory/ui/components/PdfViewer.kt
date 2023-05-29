package com.lovestory.lovestory.ui.components

import android.content.Context
import android.content.res.AssetManager
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File
import java.io.FileOutputStream

@Composable
fun PdfViewCompat(assetName: String = "lovestory_terms_of_use.pdf") {
    val context = LocalContext.current
    val assetManager: AssetManager = context.assets
    val tempFile = copyAssetToTempFile(context, assetName)
//    val fd = assetManager.openFd(assetName).parcelFileDescriptor
    val fd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
    val renderer = PdfRenderer(fd)
    val pageCount = renderer.pageCount
    val pdfBitmaps = List(pageCount) {
        renderer.openPage(it).run {
            val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
            render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            close()
            bitmap
        }
    }

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
    ) {
        val scrollState = rememberScrollState()
        Column(modifier = Modifier
            .background(Color.White)
            .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            pdfBitmaps.forEach { pdfBitmap ->
                Image(
                    bitmap = pdfBitmap!!.asImageBitmap(),
                    contentDescription = "pdf page",
                    modifier = Modifier.size(width = pdfBitmap.width.dp,height=480.dp).background(Color.White),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }

}

fun copyAssetToTempFile(context: Context, assetName: String): File {
    val inputStream = context.assets.open(assetName)
    val tempFile = File.createTempFile("temp_", ".pdf", context.cacheDir)
    val outputStream = FileOutputStream(tempFile)

    try {
        inputStream.copyTo(outputStream)
    } finally {
        inputStream.close()
        outputStream.close()
    }

    return tempFile
}