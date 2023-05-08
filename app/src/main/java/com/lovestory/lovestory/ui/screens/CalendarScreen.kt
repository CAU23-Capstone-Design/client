package com.lovestory.lovestory.ui.screens

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
//import com.google.android.gms.maps.model.CameraPosition
//import com.google.android.gms.maps.model.LatLng
//import com.google.maps.android.compose.GoogleMap
//import com.google.maps.android.compose.Marker
//import com.google.maps.android.compose.MarkerState
//import com.google.maps.android.compose.rememberCameraPositionState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.*
import com.lovestory.lovestory.model.*
import com.lovestory.lovestory.module.getSavedComment
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.module.saveComment
import com.lovestory.lovestory.resource.vitro
import com.lovestory.lovestory.ui.components.*
import com.lovestory.lovestory.ui.theme.LoveStoryTheme
import retrofit2.Response
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.compose.*
import com.lovestory.lovestory.R
import com.lovestory.lovestory.graphs.CalendarNavGraph
import com.lovestory.lovestory.graphs.MainScreens
import com.lovestory.lovestory.network.*
import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select
import okhttp3.Dispatcher
import java.time.DayOfWeek


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CalendarScreen(navHostController: NavHostController) {

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
    val daysOfWeek = remember { daysOfWeek() }

    //val navigateToMapScreen = remember { mutableStateOf(false) }
    //if (navigateToMapScreen.value) {
    //    MapScreen()
    //}

    var selectionSave by rememberSaveable { mutableStateOf(CalendarDay(date = LocalDate.now(), position = DayPosition.MonthDate))}
    var isPopupVisibleSave by rememberSaveable { mutableStateOf(false) }
    //Log.d("세이브", "$selectionSave, $isPopupVisibleSave")

    var selection by remember { mutableStateOf(CalendarDay(date = LocalDate.now(), position = DayPosition.MonthDate))}
    var isPopupVisible by remember { mutableStateOf(false) }
    //Log.d("세이브", "$selection, $isPopupVisible")
    //Log.d("셀렉션1", "${selection.date}")

    if(isPopupVisibleSave){
        selection = selectionSave
        isPopupVisible = true
    }

    val onOpenDialogRequest : ()->Unit = {
        isPopupVisible = true
        //isPopupVisibleSave = true
    }
    val onDismissRequest : () -> Unit = {isPopupVisible = false}

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth =  endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    val coroutineScope = rememberCoroutineScope()
    val visibleMonth = rememberFirstCompletelyVisibleMonth(state)

    var coupleMemoryList by remember { mutableStateOf(emptyList<CoupleMemory>()) }
    val stringMemoryList = mutableListOf<StringMemory>()

    var latLng by remember { mutableStateOf(emptyList<LatLng>()) }
    val dataLoaded = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val token = getToken(context)

    //해야 되는 게 코루틴 정리. 룸 db
    LaunchedEffect(key1 = true) {
        //내부 db
        //Log.d("세이브","$isPopupVisibleSave")
        //if(!isPopupVisibleSave) {
            val data = withContext(Dispatchers.IO) {
                getSavedComment(context)
            }
            coupleMemoryList = data
            coupleMemoryList.forEach { CoupleMemory -> Log.d("쉐어드1", "$CoupleMemory") }
            //saveComment(context, coupleMemoryList) // 이 부분 주석 처리하면 shared preference 초기화 가능
        //}

            //서버 통신
            val getMemoryList: Response<List<GetMemory>> = getComment(token!!)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            for (getMemory in getMemoryList.body()!!) {
                val date = LocalDate.parse(getMemory.date, formatter)
                val comment = getMemory.comment
                val coupleMemory = CoupleMemory(date, comment)

                val newCoupleMemoryList = coupleMemoryList.plus(coupleMemory)
                coupleMemoryList = newCoupleMemoryList
            }

            saveComment(context, coupleMemoryList)
    }



    //Log.d("토큰","$token")
/*
    LaunchedEffect(isPopupVisible){
        if(!isPopupVisible){
            /*
            val getMemoryList: Response<List<GetMemory>> = getComment(token!!)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            for (getMemory in getMemoryList.body()!!) {
                val date = LocalDate.parse(getMemory.date, formatter)
                val comment = getMemory.comment
                val coupleMemory = CoupleMemory(date, comment)

                val newCoupleMemoryList = coupleMemoryList.plus(coupleMemory)
                coupleMemoryList = newCoupleMemoryList
            }

             */
            saveComment(context, coupleMemoryList)
            //coupleMemoryList.forEach{CoupleMemory -> Log.d("서버2","$CoupleMemory") }
        }
    }
 */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        //horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // /* 캘린더 타이틀 부분, preview 할 때, 곧바로 확인이 안 되서 잠시 주석 처리
        SimpleCalendarTitle(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            currentMonth = visibleMonth.yearMonth,
            goToPrevious = {
                coroutineScope.launch {
                    state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth)
                }
            },
            goToNext = {
                coroutineScope.launch {
                    state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth)
                }
            },
        )

        // */

        Spacer(modifier = Modifier.height(10.dp))
        DaysOfWeekTitle(daysOfWeek = daysOfWeek)
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalCalendar(
            modifier = Modifier.wrapContentWidth(),//.background(color = Color.White, RoundedCornerShape(30.dp)),
            state = state,
            dayContent = { day ->
                Day(
                    day = day,
                    isPopupVisible = isPopupVisible,
                    isSelected = selection == day,
                    onOpenDialogRequest = onOpenDialogRequest,
                    coupleMemoryList = coupleMemoryList,
                ) { clicked ->
                    selection = clicked
                }
            }
        )
    }


    if (isPopupVisible || isPopupVisibleSave) {
        //getComment 서버 통신
        LaunchedEffect(true){
            val getMemoryList: Response<List<GetMemory>> = getComment(token!!)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            for (getMemory in getMemoryList.body()!!) {
                val date = LocalDate.parse(getMemory.date, formatter)
                val comment = getMemory.comment
                val stringMemory = StringMemory(date.toString(), comment)
                stringMemoryList.add(stringMemory)
            }
            coupleMemoryList = convertToCoupleMemoryList(stringMemoryList)
            coupleMemoryList.forEach { CoupleMemory -> Log.d("쉐어드3", "$CoupleMemory") }

            //get GPS
            val date = selection.date
            val formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val dateString = date.format(formatter1)
            val gps = getGps(token!!, dateString)

            latLng = if (gps.body() != null) {
                getLatLng(gps.body()!!)
            }else{
                val position = mutableListOf<LatLng>()
                position.add(LatLng(37.503735330931136, 126.95615523253305))
                position
            }
            Log.d("코루틴","호출1, $latLng")
            dataLoaded.value = true
        }

        var editedcomment by remember { mutableStateOf("") }
        val existingMemory = coupleMemoryList.firstOrNull { it.date == selection.date }
        if (existingMemory != null) {
            editedcomment = existingMemory.comment
        }

        CalendarDialog(
            selection = selection,
            onDismissRequest = {
                if(existingMemory != null) {
                    coupleMemoryList.find{ it.date == selection.date }?.comment = editedcomment
                    coroutineScope.launch{
                        val date = selection.date
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val dateString = date.format(formatter)
                        val put : Response<Any> = putComment(token!!, dateString, editedcomment)
                        //Log.d("풑1","$put, $dateString, $editedcomment")
                        saveComment(context, coupleMemoryList)
                    }
                } else {
                    if ( editedcomment != ""){
                        val newMemory = CoupleMemory(date = selection.date, comment = editedcomment)
                        coupleMemoryList = coupleMemoryList.toMutableList().apply{add(newMemory)}
                        coroutineScope.launch{
                            val date = selection.date
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            val dateString = date.format(formatter)
                            val put : Response<Any> = putComment(token!!, dateString, editedcomment)
                            //Log.d("풑2","$put, $dateString, $editedcomment")
                            saveComment(context, coupleMemoryList)
                        }
                    }
                }
                isPopupVisible = false// Update coupleMemoryList when dialog is dismissed
                isPopupVisibleSave = false
                dataLoaded.value = false
            }, //onDismissRequest,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            Column(
                modifier = Modifier
                    .width(360.dp)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(color = Color.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(color = Color.Transparent)
                        .padding(start = 25.dp, end = 25.dp, top = 15.dp, bottom = 10.dp),//vertical = 15.dp, horizontal = 25.dp),
                    verticalAlignment = Alignment.Bottom
                ){
                    Text(
                        text = selection.date.dayOfMonth.toString(),
                        fontSize = 26.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        modifier = Modifier
                            .padding(bottom = 3.dp)
                            .weight(1f),
                        text = selection.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())+"요일",
                        fontSize = 16.sp,
                        color = Color.Black,
                    )
                    //Spacer(Modifier.weight(1f))
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                        onClick = {
                            coroutineScope.launch {
                                val date = selection.date
                                coupleMemoryList = coupleMemoryList.filterNot { it.date == date }

                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                val dateString = date.format(formatter)
                                val delete: Any = deleteComment(token!!, dateString)
                                Log.d("삭제", "$delete, $dateString")
                            }
                            saveComment(context, coupleMemoryList)
                            isPopupVisible = false
                            isPopupVisibleSave = false
                            dataLoaded.value = false
                        },
                        elevation = null,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp),
                        //.padding(bottom = 5.dp),//wrapContentSize(),
                        shape = CircleShape,
                    ){
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_delete),
                            contentDescription = "Delete"
                        )
                    }

                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                        onClick = {
                            if(existingMemory != null) {
                                coupleMemoryList.find{ it.date == selection.date }?.comment = editedcomment
                                //sendComment
                                coroutineScope.launch{
                                    val date = selection.date
                                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                    val dateString = date.format(formatter)
                                    val put : Response<Any> = putComment(token!!, dateString, editedcomment)
                                    //Log.d("풑3","$put, $dateString, $editedcomment")
                                    saveComment(context, coupleMemoryList)
                                }
                            } else {
                                if ( editedcomment != ""){
                                    val newMemory = CoupleMemory(date = selection.date, comment = editedcomment)
                                    coupleMemoryList = coupleMemoryList.toMutableList().apply{add(newMemory)}
                                    coroutineScope.launch{
                                        val date = selection.date
                                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        val dateString = date.format(formatter)
                                        val put : Response<Any> = putComment(token!!, dateString, editedcomment)
                                        //Log.d("풑4","$put, $dateString, $editedcomment")
                                        saveComment(context, coupleMemoryList)
                                    }
                                }
                            }
                            isPopupVisible = false
                            isPopupVisibleSave = false
                        },
                        elevation = null,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp)
                            .padding(bottom = 5.dp),//wrapContentSize(),
                        shape = CircleShape,
                    ){
                        Text(
                            text = "X",
                            fontSize = 22.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(start = 20.dp, end = 20.dp))
                Spacer(modifier = Modifier.height(15.dp))
                EditableTextField(
                    initialValue = editedcomment,
                    onValueChanged = {editedcomment = it}
                )
                //Text(text = "Current value: $editedcomment")
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp)
                        .height(300.dp)
                        .background(color = Color.LightGray, RoundedCornerShape(12.dp))
                ){
                    var viewposition = LatLng(37.503735330931136, 126.95615523253305)
                    var cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(viewposition, 15f)
                    }
                    selectionSave = selection

                    if (!dataLoaded.value) {
                        //스켈레톤 추가
                        Box(modifier = Modifier.fillMaxSize().background(color = Color.Transparent))
                    } else {
                        viewposition = averageLatLng(latLng)
                        cameraPositionState = CameraPositionState(position = CameraPosition.fromLatLngZoom(viewposition, 15f))
                        Log.d("좌표","$viewposition, $latLng")
                        GoogleMap(
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                            cameraPositionState = cameraPositionState,
                            onMapClick = {
                                isPopupVisible = false
                                isPopupVisibleSave = true
                                navHostController.navigate(MainScreens.Map.route) {
                                    launchSingleTop = true
                                    Log.d("클릭","클릭")
                                }
                            },
                            uiSettings = uiSettings
                        ) {
                            if(latLng.isNotEmpty()) {
                                Polyline(
                                    points = latLng,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

val uiSettings = MapUiSettings(
        compassEnabled = false,
        indoorLevelPickerEnabled = false,
        mapToolbarEnabled = false,
        myLocationButtonEnabled = false,
        rotationGesturesEnabled = false,
        scrollGesturesEnabled = false,
        scrollGesturesEnabledDuringRotateOrZoom = false,
        tiltGesturesEnabled = false,
        zoomControlsEnabled = false,
        zoomGesturesEnabled = false
    )




@Preview(showSystemUi = true)
@Composable
fun DefaultPreview() {
    val navController = rememberNavController()
    LoveStoryTheme {
        //CalendarScreen(navHostController = navController, onNavigateToMapScreen = )
    }
}

