package com.lovestory.lovestory.ui.screens

import android.os.Vibrator
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.lovestory.lovestory.module.photo.deletePhotosByIds
import com.lovestory.lovestory.ui.components.*
import com.lovestory.lovestory.view.SyncedPhotoView
import kotlinx.coroutines.withContext
import java.time.LocalDate

@Composable
fun GalleryScreen(navHostController: NavHostController, syncedPhotoView : SyncedPhotoView) {
//    val syncedPhotos by syncedPhotoView.listOfSyncPhotos.observeAsState(initial = listOf())
//    val daySyncedPhoto by syncedPhotoView.dayListOfSyncedPhotos.observeAsState(initial = listOf())
    val syncedPhotosByDate by syncedPhotoView.groupedSyncedPhotosByDate.observeAsState(initial = mapOf())
    val daySyncedPhotosByDate by syncedPhotoView.daySyncedPhotosByDate.observeAsState(initial = mapOf())
    val monthSyncedPhotosByDate by syncedPhotoView.monthListOfSyncedPhotos.observeAsState(initial = listOf())
    val yearSyncedPhotosByDate by syncedPhotoView.yearListOfSyncedPhotos.observeAsState(initial = listOf())

//    val syncedPhotosByDateAndArea by syncedPhotoView.syncedPhotosByDateAndArea.observeAsState(initial = mapOf())

    val sizesOfInnerElements by syncedPhotoView.sizesOfInnerElements.observeAsState(initial = listOf())

    val cumOfSizeOfInnerElements by syncedPhotoView.cumOfSizeOfInnerElements.observeAsState(initial = syncedPhotoView.computeCumulativeSizes(sizesOfInnerElements))

//    val listOfSelectedPhoto : MutableSet<String> = remember {
//        mutableSetOf()
//    }

    val listOfSelectedPhoto = remember{ mutableStateOf<MutableSet<String>>(mutableSetOf()) }

    val systemUiController = rememberSystemUiController()

    val context = LocalContext.current

    val currentDate = LocalDate.now()
    val token = getToken(context)

    val allPhotoListState = rememberLazyListState()
    val dayPhotoListState = rememberLazyListState()
    val monthPhotoListState = rememberLazyListState()
    val yearPhotoListState = rememberLazyListState()

    val isPressedPhotoMode = remember { mutableStateOf(false) }
    val isDropMenuForGalleryScreen = remember { mutableStateOf(false) }

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
                allPhotoListState = allPhotoListState,
                syncedPhotoView = syncedPhotoView,
                isPressedPhotoMode = isPressedPhotoMode,
                listOfSelectedPhoto = listOfSelectedPhoto.value
            )
        }

        AnimatedVisibility(visible = selectedButton =="일",
//            enter = fadeIn(), exit = fadeOut()
        ) {
            DayGroupedGallery(
                daySyncedPhotoByDate = daySyncedPhotosByDate,
                token = token,
                currentDate = currentDate,
                allPhotoListState = allPhotoListState,
                dayPhotoListState = dayPhotoListState,
                setSelectedButton = setSelectedButton,
                cumOfSizeOfInnerElements = cumOfSizeOfInnerElements
            )

        }

        AnimatedVisibility(visible = selectedButton =="월") {
            RepresentPeriodGallery(
                periodGallery = monthSyncedPhotosByDate,
                token = token,
                currentDate = currentDate
            )
        }

        AnimatedVisibility(visible = selectedButton =="년") {
            RepresentPeriodGallery(
                periodGallery = yearSyncedPhotosByDate,
                token = token,
                currentDate = currentDate
            )
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
                AnimatedVisibility(
                    visible = !isPressedPhotoMode.value,
                    enter = fadeIn(),
                    exit = fadeOut()) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "갤러리",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row() {
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
                            
                            Spacer(modifier = Modifier.width(20.dp))

                            Box() {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_more_vert_24),
                                    contentDescription = null,
                                    modifier = Modifier.clickable {isDropMenuForGalleryScreen.value = true},
                                    tint = Color.Black
                                )
                                DropdownMenu(
                                    expanded = isDropMenuForGalleryScreen.value,
                                    onDismissRequest = { isDropMenuForGalleryScreen.value = false },
                                    modifier = Modifier.wrapContentSize()
                                ) {
                                    DropdownMenuItem(
                                        onClick = {
                                            isDropMenuForGalleryScreen.value = false
                                            isPressedPhotoMode.value = true
                                        },
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        ) {
                                            Text(text = "사진  삭제" ,textAlign = TextAlign.Center)
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
                AnimatedVisibility(visible = isPressedPhotoMode.value, enter = fadeIn(), exit = fadeOut()){
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        Box() {
                            Text(
                                text = "취소",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    listOfSelectedPhoto.value.clear()
                                    isPressedPhotoMode.value = false
                                }
                            )
                        }
                        Box() {
                            val countSelected = listOfSelectedPhoto.value.size
                            Text(
                                text = "${countSelected}장 삭제",
                                fontSize = 18.sp,
                                color = Color.Red,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    CoroutineScope(Dispatchers.IO).launch{
                                        deletePhotosByIds(context, listOfSelectedPhoto.value)
                                        withContext(Dispatchers.Main) {
                                            isPressedPhotoMode.value = false
                                            Toast.makeText(context, "사진 삭제 되었습니다.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // floating bar Section
            Spacer(modifier = Modifier.weight(1f))
            AnimatedVisibility(visible = !isPressedPhotoMode.value, enter= fadeIn(), exit = fadeOut()) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 10.dp, end=10.dp,bottom = 80.dp)
                ) {
                    SelectMenuButtons(
                        items = items,
                        selectedButton = selectedButton,
                        onClick ={ item : String ->
                            isPressedPhotoMode.value = false
                            setSelectedButton(item)
                        })

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
}