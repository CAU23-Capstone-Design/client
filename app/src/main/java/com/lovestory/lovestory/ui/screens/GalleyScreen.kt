package com.lovestory.lovestory.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
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
import androidx.navigation.NavHostController
import com.lovestory.lovestory.resource.vitro
import com.lovestory.lovestory.ui.components.DisplayImageFromUri
import com.lovestory.lovestory.view.PhotoView
import com.lovestory.lovestory.graphs.GalleryStack
import com.lovestory.lovestory.graphs.MainScreens
import com.lovestory.lovestory.module.checkExistNeedPhotoForSync
import com.lovestory.lovestory.view.ImageSyncView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun GalleryScreen(navHostController: NavHostController, galleryView : PhotoView, imageSyncView : ImageSyncView) {
    val syncedPhotos by galleryView.syncedPhotos.observeAsState(initial = listOf())
//    val downloadStatus by imageSyncView.downloadStatus.observeAsState("")
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ){
        Row() {
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
            Spacer(modifier = Modifier.width(10.dp))
            Button(onClick = {
                val token = "your_jwt_token"
                val photoId = "your_photo_id"
                CoroutineScope(Dispatchers.IO).launch{
                    checkExistNeedPhotoForSync(context, imageSyncView)
                }
//
//            imageSyncView.getImageFromServer(token, photoId)
            }) {
                Text("동기화 하기")
            }
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


//        if (downloadStatus.isNotEmpty()) {
//            LaunchedEffect(key1 = downloadStatus) {
//                Toast.makeText(context, downloadStatus, Toast.LENGTH_SHORT).show()
//            }
//        }
    }
}