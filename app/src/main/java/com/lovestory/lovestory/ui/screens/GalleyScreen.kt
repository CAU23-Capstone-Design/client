package com.lovestory.lovestory.ui.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.exifinterface.media.ExifInterface
import androidx.navigation.NavHostController
import com.lovestory.lovestory.resource.apple_bold
import com.lovestory.lovestory.resource.vitro
import com.lovestory.lovestory.ui.components.DisplayImageFromUri
import com.lovestory.lovestory.view.PhotoViewModel
import com.lovestory.lovestory.entity.Photo
import com.lovestory.lovestory.graphs.GalleryStack
import com.lovestory.lovestory.graphs.MainScreens

@Composable
fun GalleryScreen(navHostController: NavHostController, viewModel: PhotoViewModel) {
    val syncedPhotos by viewModel.syncedPhotos.observeAsState(initial = listOf())
//    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ){
        Text(text = "Gallery",
            fontSize = 30.sp,
            fontFamily = vitro,
            fontWeight = FontWeight.Normal)
        Button(
            onClick = { navHostController.navigate(GalleryStack.PhotoSync.route){
                popUpTo(MainScreens.Gallery.route)
            } },
        ){
            Text(text = "이미지 업로드 하기")
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp)
        ) {
            items(syncedPhotos.size) { index ->
                DisplayImageFromUri(
                    index = index,
                    imageUri = syncedPhotos[index].imageUrl.toString(),
                )
            }
        }
    }
}