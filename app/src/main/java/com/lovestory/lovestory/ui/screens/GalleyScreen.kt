package com.lovestory.lovestory.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lovestory.lovestory.ui.components.DisplayImageFromUri
import com.lovestory.lovestory.view.PhotoView
import com.lovestory.lovestory.graphs.GalleryStack
import com.lovestory.lovestory.graphs.MainScreens
import com.lovestory.lovestory.module.checkExistNeedPhotoForSync
import com.lovestory.lovestory.view.ImageSyncView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.lovestory.lovestory.R
import com.lovestory.lovestory.ui.components.SelectMenuButtons

@Composable
fun GalleryScreen(navHostController: NavHostController, galleryView : PhotoView, imageSyncView : ImageSyncView) {
    val syncedPhotos by galleryView.syncedPhotos.observeAsState(initial = listOf())
//    val downloadStatus by imageSyncView.downloadStatus.observeAsState("")
    val context = LocalContext.current

    val (selectedButton, setSelectedButton) = remember { mutableStateOf("전체") }
    val items = listOf<String>(
        "년", "월", "일", "전체"
    )

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        // Gallery List
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .padding(start = 2.dp, end = 2.dp, top = 48.dp, bottom = 70.dp)
                .fillMaxSize(),
        ) {
            items(syncedPhotos.size) { index ->
                DisplayImageFromUri(
                    index = index,
                    imageUri = syncedPhotos[index].imageUrl.toString(),
                )
            }
        }
        // gallery header and floating bar Section
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // gallery header
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0xFFF3F3F3))
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "갤러리",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    painter = painterResource(id = R.drawable.baseline_sync_24),
                    contentDescription = "sync photo",
                    modifier = Modifier.clickable { CoroutineScope(Dispatchers.IO).launch {
                        checkExistNeedPhotoForSync(context, imageSyncView)
                    } }
                )
            }

            // floating bar Section
            Spacer(modifier = Modifier.weight(1f))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 10.dp, end=10.dp,bottom = 80.dp)
            ) {
                SelectMenuButtons(items = items, selectedButton = selectedButton, setSelectedButton = setSelectedButton)

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        navHostController.navigate(GalleryStack.PhotoSync.route) {
                            popUpTo(MainScreens.Gallery.route)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFEEC9C9)),
                    modifier = Modifier
                        .height(55.dp)
                        .width(55.dp),
                    shape = RoundedCornerShape(40.dp),
                    content = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_upload_24),
                            contentDescription = "upload photo"
                        )
                    }
                )
            }
        }
    }
}


//        // gallery Layout Container
//        Column(
//            verticalArrangement = Arrangement.Top,
//            horizontalAlignment = Alignment.Start,
//            modifier = Modifier
//                .background(Color.White)
//                .fillMaxSize()
//
//        ){
//
//
//
//            // gallery header
//            Column() {
//                Row(
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    modifier = Modifier.background(Color(0X55F3F3F3))
//                ){
//                    Text(text = "갤러리",
//                        fontSize = 24.sp,
//                        fontWeight = FontWeight.Bold)
//
//                    Button(
//                        onClick = {
//                            CoroutineScope(Dispatchers.IO).launch{
//                                checkExistNeedPhotoForSync(context, imageSyncView)
//                            }
//                        },
//                        modifier = Modifier
//                            .padding(start = 70.dp),
////                            .align(Alignment.CenterHorizontally),
//                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFEEC9C9)),
//                        shape = RoundedCornerShape(25.dp),
//                        content = {
//                            Icon(
//                                painter = painterResource(id = R.drawable.baseline_sync_24),
//                                contentDescription = "sync photo"
//                            )
//                        }
//                    )
//
//                }
//
//            }
//
//
//
//            // floating bar Section
//            Row(){
//                Box() {
//                    Button(
//                        onClick = { navHostController.navigate(GalleryStack.PhotoSync.route){
//                            popUpTo(MainScreens.Gallery.route)
//                        } },
//                        modifier = Modifier
//                            .padding(bottom = 70.dp),
////                            .align(Alignment.CenterHorizontally),
//                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFEEC9C9)),
//                        shape = RoundedCornerShape(25.dp),
//                        content = {
//                            Icon(
//                                painter = painterResource(id = R.drawable.baseline_upload_24),
//                                contentDescription = "upload photo"
//                            )
//                        }
//                    )
//                }
//            }
//        }

//        if (downloadStatus.isNotEmpty()) {
//            LaunchedEffect(key1 = downloadStatus) {
//                Toast.makeText(context, downloadStatus, Toast.LENGTH_SHORT).show()
//            }
//        }