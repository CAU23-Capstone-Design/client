package com.lovestory.lovestory.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.lovestory.lovestory.R
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.ui.components.*
import com.lovestory.lovestory.view.SyncedPhotoView
import kotlinx.coroutines.*
import java.time.LocalDate

@Composable
fun GalleryScreen(navHostController: NavHostController, syncedPhotoView : SyncedPhotoView) {

    val sizesOfInnerElements by syncedPhotoView.sizesOfInnerElements.observeAsState(initial = listOf())
    val cumOfSizeOfInnerElements by syncedPhotoView.cumOfSizeOfInnerElements.observeAsState(
        initial = syncedPhotoView.computeCumulativeSizes(sizesOfInnerElements)
    )

    val daySyncedPhotosByDate by syncedPhotoView.daySyncedPhotosByDate.observeAsState(initial = mapOf())
    val sizeOfDaySyncedPhotos by syncedPhotoView.sizeOfDaySyncedPhotos.observeAsState(initial = listOf())
    val cumOfDaySyncedPhotos by syncedPhotoView.cumOfDaySyncedPhotos.observeAsState(
        initial = syncedPhotoView.computeCumulativeSizesForMonth(sizeOfDaySyncedPhotos)
    )

    val monthSyncedPhotosByDate by syncedPhotoView.monthListOfSyncedPhotos.observeAsState(initial = listOf())
    val sizeOfMonthSyncedPhotos by syncedPhotoView.sizeOfMonthSyncedPhotos.observeAsState(initial = listOf())
    val cumOfMonthSyncedPhotos by syncedPhotoView.cumOfMonthSyncedPhotos.observeAsState(
        initial = syncedPhotoView.computeCumulativeSizesForMonth(sizeOfMonthSyncedPhotos)
    )

    val yearSyncedPhotosByDate by syncedPhotoView.yearListOfSyncedPhotos.observeAsState(initial = listOf())

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
    val countSelectedPhotos = remember { mutableStateOf(0) }
    val (selectedButton, setSelectedButton) = remember { mutableStateOf("전체") }
    val showDeleteSyncedPhotoDialog = remember { mutableStateOf(false) }

    val items = listOf<String>(
        "년", "월", "일", "전체"
    )

    LaunchedEffect(null){
        syncedPhotoView.selectedPhotosSet.value = mutableSetOf()
    }

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF3F3F3),
        )
    }

    BackHandler(enabled = true) {
        if(isPressedPhotoMode.value){
            syncedPhotoView.clearSelectedPhotosSet()
            isPressedPhotoMode.value = false
            countSelectedPhotos.value = 0
        }else{
            navHostController.popBackStack()
        }
    }

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        AnimatedVisibility (visible = showDeleteSyncedPhotoDialog.value, enter = fadeIn(), exit = fadeOut()){
            DeleteSyncPhotosDialog(
                showDeleteSyncedPhotoDialog = showDeleteSyncedPhotoDialog,
                isPressedPhotoMode = isPressedPhotoMode,
                countSelectedPhotos = countSelectedPhotos,
                syncedPhotoView = syncedPhotoView,
                context = context
            )
        }

        // Gallery List
        AnimatedVisibility(visible = selectedButton =="전체",
        ){
            GroupedGallery(
                token = token,
                navHostController = navHostController,
                currentDate = currentDate,
                allPhotoListState = allPhotoListState,
                syncedPhotoView = syncedPhotoView,
                isPressedPhotoMode = isPressedPhotoMode,
                listOfSelectedPhoto = syncedPhotoView.selectedPhotosSet,
                countSelectedPhotos = countSelectedPhotos
            )
        }

        AnimatedVisibility(visible = selectedButton =="일") {
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
                prevPhotoState = dayPhotoListState,
                currentPhotoState = monthPhotoListState,
                setSelectedButton = setSelectedButton,
                cumPrevPhotosList = cumOfDaySyncedPhotos,
                type = "월"
            )
        }

        AnimatedVisibility(visible = selectedButton =="년") {
            RepresentPeriodGallery(
                periodGallery = yearSyncedPhotosByDate,
                token = token,
                prevPhotoState = monthPhotoListState,
                setSelectedButton = setSelectedButton,
                currentPhotoState = yearPhotoListState,
                cumPrevPhotosList = cumOfMonthSyncedPhotos,
                type = "년"
            )
        }

        // gallery header and floating bar Section
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            GalleryHeader(
                context,
                isPressedPhotoMode,
                isDropMenuForGalleryScreen,
                selectedButton,
                syncedPhotoView,
                countSelectedPhotos,
                showDeleteSyncedPhotoDialog
            )

            Spacer(modifier = Modifier.weight(1f))

            AnimatedVisibility(visible = !isPressedPhotoMode.value, enter= fadeIn(), exit = fadeOut()) {
                FloatingSection(
                    items = items,
                    selectedButton = selectedButton,
                    isPressedPhotoMode = isPressedPhotoMode,
                    setSelectedButton = setSelectedButton,
                    navHostController = navHostController
                )
            }

        }
    }
}

@Composable
fun GalleryHeader(
    context: Context,
    isPressedPhotoMode : MutableState<Boolean>,
    isDropMenuForGalleryScreen : MutableState<Boolean>,
    selectedButton : String,
    syncedPhotoView : SyncedPhotoView,
    countSelectedPhotos : MutableState<Int>,
    showDeleteSyncedPhotoDialog : MutableState<Boolean>,
){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color(0xBBF3F3F3))
            .fillMaxWidth()
            .height(60.dp)
            .padding(start = 10.dp, end = 10.dp)
    ) {
        AnimatedVisibility( visible = !isPressedPhotoMode.value, enter = fadeIn(), exit = fadeOut()) {
            HeaderForGallery(
                context = context,
                isDropMenuForGalleryScreen = isDropMenuForGalleryScreen,
                isPressedPhotoMode = isPressedPhotoMode,
                selectedButton = selectedButton,
            )
        }
        AnimatedVisibility(visible = isPressedPhotoMode.value, enter = fadeIn(), exit = fadeOut()){
            HeaderForDeletePhoto(
                context = context,
                syncedPhotoView = syncedPhotoView,
                isPressedPhotoMode = isPressedPhotoMode,
                countSelectedPhotos = countSelectedPhotos,
                showDeleteSyncedPhotoDialog = showDeleteSyncedPhotoDialog
            )
        }
    }
}

@Composable
fun FloatingSection(
    items : List<String>,
    selectedButton : String,
    isPressedPhotoMode: MutableState<Boolean>,
    setSelectedButton: (String) -> Unit,
    navHostController: NavHostController
){
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

        Spacer(modifier = Modifier.weight(0.5f))

        Button(
            onClick = {
                navHostController.navigate(GalleryStack.PhotoSync.route) {
                    popUpTo(MainScreens.Gallery.route)
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFCC5C5)),
            modifier = Modifier
                .height(50.dp)
                .width(50.dp),
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

@Composable
fun HeaderForGallery(
    context: Context,
    isDropMenuForGalleryScreen : MutableState<Boolean>,
    isPressedPhotoMode : MutableState<Boolean>,
    selectedButton : String
){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "갤러리",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 10.dp)
        )

        AnimatedVisibility(visible = selectedButton == "전체", enter = fadeIn(), exit = fadeOut()) {
            Row() {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_sync_24),
                    contentDescription = "sync photo",
                    modifier = Modifier.clip(shape = CircleShape).clickable {
                        Toast.makeText(context,"사진 동기화를 시작합니다.", Toast.LENGTH_SHORT).show()
                        CoroutineScope(Dispatchers.IO).launch {
                            checkExistNeedPhotoForSync(context)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "사진 동기화가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.padding(10.dp)
                )

//                Spacer(modifier = Modifier.width(20.dp))

                Box() {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_more_vert_24),
                        contentDescription = null,
                        modifier = Modifier.clip(shape = CircleShape).clickable {isDropMenuForGalleryScreen.value = true}.padding(10.dp),
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
}

@Composable
fun HeaderForDeletePhoto(
    context : Context,
    syncedPhotoView : SyncedPhotoView,
    isPressedPhotoMode : MutableState<Boolean>,
    countSelectedPhotos :  MutableState<Int>,
    showDeleteSyncedPhotoDialog : MutableState<Boolean>
){
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
                modifier = Modifier.clip(shape = CircleShape).clickable {
                    syncedPhotoView.clearSelectedPhotosSet()
                    isPressedPhotoMode.value = false
                    countSelectedPhotos.value = 0
                }.padding(10.dp)
            )
        }
        Box() {
            Text(
                text = "${countSelectedPhotos.value}장의 사진 삭제",
                fontSize = 18.sp,
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clip(shape = CircleShape).clickable {
                    if(countSelectedPhotos.value >0){
                        showDeleteSyncedPhotoDialog.value = true
                    }
                    else{
                        Toast
                            .makeText(context, "선택된 사진이 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }.padding(10.dp)
            )
        }
    }
}
