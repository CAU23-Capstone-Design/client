package com.lovestory.lovestory.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.lovestory.lovestory.graphs.GalleryStack
import com.lovestory.lovestory.graphs.MainScreens
import com.lovestory.lovestory.module.checkExistNeedPhotoForSync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.lovestory.lovestory.R
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.ui.components.*
import com.lovestory.lovestory.view.SyncedPhotoView
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun GalleryScreen(navHostController: NavHostController, syncedPhotoView : SyncedPhotoView) {
//    val syncedPhotos by syncedPhotoView.listOfSyncPhotos.observeAsState(initial = listOf())
//    val daySyncedPhoto by syncedPhotoView.dayListOfSyncedPhotos.observeAsState(initial = listOf())
    val syncedPhotosByDate by syncedPhotoView.groupedSyncedPhotosByDate.observeAsState(initial = mapOf())
    val daySyncedPhotosByDate by syncedPhotoView.daySyncedPhotosByDate.observeAsState(initial = mapOf())

    val syncedPhotosByDateAndArea by syncedPhotoView.syncedPhotosByDateAndArea.observeAsState(initial = mapOf())

    val sizesOfInnerElements by syncedPhotoView.sizesOfInnerElements.observeAsState(initial = listOf())
    val cumOfSizeOfInnerElements by syncedPhotoView.cumOfSizeOfInnerElements.observeAsState(initial = syncedPhotoView.computeCumulativeSizes(sizesOfInnerElements))

    val systemUiController = rememberSystemUiController()


    val context = LocalContext.current
    val currentDate = LocalDate.now()
    val token = getToken(context)

    val allPhotoListState = rememberLazyListState()

    val listState = rememberLazyListState()

    val (selectedButton, setSelectedButton) = remember { mutableStateOf("전체") }
    val items = listOf<String>(
        "년", "월", "일", "전체"
    )

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
        AnimatedVisibility(visible = selectedButton =="전체",
//            enter = fadeIn(), exit = fadeOut()
        ){
            GroupedGallery(
                syncedPhotosByDate = syncedPhotosByDate,
                token = token,
                navHostController = navHostController,
                currentDate = currentDate,
                allPhotoListState = allPhotoListState)
        }

        AnimatedVisibility(visible = selectedButton =="일",
//            enter = fadeIn(), exit = fadeOut()
        ) {
            DayGroupedGallery(
                daySyncedPhotoByDate = daySyncedPhotosByDate,
                token = token,
                currentDate = currentDate,
                allPhotoListState= allPhotoListState,
                setSelectedButton = setSelectedButton,
                cumOfSizeOfInnerElements = cumOfSizeOfInnerElements
            )

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