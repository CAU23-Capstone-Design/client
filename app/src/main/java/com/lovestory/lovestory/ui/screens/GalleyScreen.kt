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
fun GalleryScreen(navHostController: NavHostController, viewModel: PhotoViewModel, syncedPhotos : List<Photo>) {
    val context = LocalContext.current
    Log.d("Gallery-Screen", "갤러리 스크린 호출")

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")

            getRotationFromImageUri(uri = uri, context = context)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

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
//        Button(
//            onClick = {  pickMedia.launch("image/*")},
//        ){
//            Text(
//                text = "사진 가져오기",
//                modifier = Modifier.padding(start = 15.dp),
//                fontFamily = apple_bold,
//                fontWeight = FontWeight.Bold,
//                fontSize = 16.sp,
//            )
//        }
//        LazyColumn {
//            items(allPhotos.size) { index ->
//                Text(text = allPhotos[index].id)
//                Text(text = allPhotos[index].date.toString())
//                DisplayImageFromUri(imageUri = allPhotos[index].imageUrl.toString())
//                Text(text = allPhotos[index].imageUrl.toString())
//                Text(text = allPhotos[index].location.toString())
//                Text(text = allPhotos[index].isSynced.toString())
//                Text(text = allPhotos[index].latitude.toString())
//                Text(text = allPhotos[index].longitude.toString())
//                Divider(color = Color.Gray, modifier = Modifier
//                    .fillMaxWidth()
//                    .height(1.dp)
//                )
//            }
//        }
    }
}

fun getRotationFromImageUri(context: Context, uri: Uri) {
    val inputStream = context.contentResolver.openInputStream(uri)
    val exifInterface = inputStream?.let { ExifInterface(it) }

    val orientation = exifInterface?.getAttribute(ExifInterface.TAG_ORIENTATION)
    val dateTaken = exifInterface?.getAttribute(ExifInterface.TAG_DATETIME)

    val latitude = exifInterface?.getAttribute(android.media.ExifInterface.TAG_GPS_LATITUDE)
    val longitude = exifInterface?.getAttribute(android.media.ExifInterface.TAG_GPS_LONGITUDE)

    Log.e("dsfgsdj.kfbgjzdbfgjkbfdg", "$orientation &&&& $dateTaken")
    Log.d("getPhotoeExif", "$latitude - $longitude")
}