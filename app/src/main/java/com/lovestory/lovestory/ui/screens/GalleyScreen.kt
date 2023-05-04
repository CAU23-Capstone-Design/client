package com.lovestory.lovestory.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lovestory.lovestory.ui.components.DisplayImageFromUri
import com.lovestory.lovestory.graphs.GalleryStack
import com.lovestory.lovestory.graphs.MainScreens
import com.lovestory.lovestory.module.checkExistNeedPhotoForSync
import com.lovestory.lovestory.view.ImageSyncView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.lovestory.lovestory.R
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.ui.components.GroupedGallery
import com.lovestory.lovestory.ui.components.SelectMenuButtons
import com.lovestory.lovestory.ui.components.ThumbnailOfPhotoFromServer
import com.lovestory.lovestory.view.SyncedPhotoView
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun GalleryScreen(navHostController: NavHostController, syncedPhotoView : SyncedPhotoView) {
    val syncedPhotos by syncedPhotoView.listOfSyncPhotos.observeAsState(initial = listOf())
    val daySyncedPhoto by syncedPhotoView.dayListOfSyncedPhotos.observeAsState(initial = listOf())

    val systemUiController = rememberSystemUiController()

    val syncedPhotosByDate = syncedPhotos.groupBy { it.date.substring(0, 10) }

    val context = LocalContext.current

    val listState = rememberLazyGridState()

    val (selectedButton, setSelectedButton) = remember { mutableStateOf("전체") }
    val items = listOf<String>(
        "년", "월", "일", "전체"
    )

    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val outputFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일")

    val token = getToken(context)

    var offset  = 0
    var photoIndex = 0

    val currentDate = LocalDate.now()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF3F3F3),
        )
    }

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        // Gallery List
        AnimatedVisibility(visible = selectedButton =="전체"){
            GroupedGallery(syncedPhotosByDate = syncedPhotosByDate, token = token, navHostController = navHostController, currentDate = currentDate)
        }

        AnimatedVisibility(visible = selectedButton =="일") {
            LazyColumn(
                modifier = Modifier
                    .padding(start = 2.dp, end = 2.dp, bottom = 70.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(top=65.dp, bottom = 75.dp),
                userScrollEnabled = true,

                ) {
                items(daySyncedPhoto.size) { index ->
                    if (token != null) {
                        ThumbnailOfPhotoFromServer(index = index, token = token, photoId = daySyncedPhoto[index].id, navHostController= navHostController)
                    }
                }
            }
        }

        AnimatedVisibility(visible = selectedButton =="월") {
            Text(text = "월")
        }

        AnimatedVisibility(visible = selectedButton =="년") {
            Text(text = "년")
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
                    .background(Color(0xBBF3F3F3))
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 20.dp)
            ) {
                Text(
//                    text = "갤러리 ${if(syncedPhotos.isNotEmpty()) LocalDateTime.parse(syncedPhotos[listState.firstVisibleItemIndex].date, inputFormatter).format(outputFormatter) else ""}",
                    text = "갤러리",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    painter = painterResource(id = R.drawable.baseline_sync_24),
                    contentDescription = "sync photo",
                    modifier = Modifier.clickable {
                        Toast.makeText(context,"사진 동기화를 시작합니다.", Toast.LENGTH_SHORT).show()
                        CoroutineScope(Dispatchers.IO).launch {
                            checkExistNeedPhotoForSync(context)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "사진 동기화가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
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
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFCC5C5)),
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