package com.lovestory.lovestory.ui.screens

import android.util.Log
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
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
import com.lovestory.lovestory.model.CoupleMemory
import com.lovestory.lovestory.model.generateCoupleMemory
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.network.getComment
import com.lovestory.lovestory.resource.vitro
import com.lovestory.lovestory.ui.components.*
import com.lovestory.lovestory.ui.theme.LoveStoryTheme
import kotlinx.coroutines.launch
import retrofit2.Response
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun CalendarScreen(navHostController: NavHostController) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
    val daysOfWeek = remember { daysOfWeek() }

    var selection by remember { mutableStateOf(CalendarDay(date = LocalDate.now(), position = DayPosition.MonthDate))}

    var isPopupVisible by remember { mutableStateOf(false) }

    val onOpenDialogRequest : ()->Unit = {isPopupVisible = true}
    val onDismissRequest : () -> Unit = {isPopupVisible = false}

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth =  endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    val coroutineScope = rememberCoroutineScope()
    val visibleMonth = rememberFirstCompletelyVisibleMonth(state)

    var coupleMemoryList by remember { mutableStateOf(generateCoupleMemory()) }
    //var selectedMemory = coupleMemoryList.find{it.date
    //
    // == selection.date}

    val context = LocalContext.current
    val token = getToken(context)
    Log.d("토큰","$token")
    LaunchedEffect(isPopupVisible){
        if(isPopupVisible){
            if(token != null) {
                val any: Response<Any> = getComment(token)
                Log.d("코멘트", "${any.body()}")
            }
        }
    }
    //var comment = getComment(token)

    //coupleMemoryList.forEach{coupleMemory -> Log.d("업뎃","$coupleMemory") }
    //Log.d("셀렉트 메모리","$selectedMemory")

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
                    isSelected = selection == day,
                    onOpenDialogRequest = onOpenDialogRequest,
                    coupleMemoryList = coupleMemoryList,
                ) { clicked ->
                    selection = clicked
                }
            }
        )
    }

    //Log.d("tag","$selection") //CalendarDay(date=2023-03-08, position=MonthDate)
    //Log.d("tag","$selectedMemory")
    //Log.d("popup","$isPopupVisible")

    if (isPopupVisible){
        //getComment

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
                    //sendComment
                } else {
                    if ( editedcomment != ""){
                        val newMemory = CoupleMemory(date = selection.date, comment = editedcomment)
                        coupleMemoryList = coupleMemoryList.toMutableList().apply{add(newMemory)}

                    }
                }
                isPopupVisible = false// Update coupleMemoryList when dialog is dismissed
            }, //onDismissRequest,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            /*
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onDismissRequest() }
                        )
                    },
                    //.clickable(onClick = onDismissRequest, indication = null), // This makes the dialog dismiss on outside click
                color = Color.Transparent, // This makes the background of the Surface transparent
            ) {

             */
                //Box(
                //    modifier = Modifier.wrapContentSize(),//.clickable(onClick = {onDismissRequest}),
                //    contentAlignment = Alignment.Center
                //) {
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
                            //horizontalArrangement = Arrangement.SpaceBetween,
                            //verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                text = selection.date.dayOfMonth.toString(),
                                fontSize = 26.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold,
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                modifier = Modifier.padding(bottom = 3.dp).weight(1f),
                                text = selection.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())+"요일",
                                fontSize = 16.sp,
                                color = Color.Black,
                            )
                            //Spacer(Modifier.weight(1f))
                            Button(
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                                onClick = {
                                    if(existingMemory != null) {
                                        coupleMemoryList.find{ it.date == selection.date }?.comment = editedcomment
                                        //sendComment
                                    } else {
                                        if ( editedcomment != ""){
                                            val newMemory = CoupleMemory(date = selection.date, comment = editedcomment)
                                            coupleMemoryList = coupleMemoryList.toMutableList().apply{add(newMemory)}

                                        }
                                    }
                                    isPopupVisible = false
                                },
                                elevation = null,
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.width(30.dp).height(30.dp).padding(bottom = 5.dp),//wrapContentSize(),
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
                        Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(start = 15.dp, end = 15.dp))
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
                                .background(color = Color.Gray, RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            //}
        //}
    }
}

/*
//사용자 위치 접근 권한 요청
val permissionState = rememberPermissionState(
    permission = Manifest.permission.ACCESS_FINE_LOCATION,
    rationale = PermissionRationale(
        title = "Location Permission",
        message = "This app needs access to your location to provide the weather forecast."
    )
)
if (permissionState.hasPermission) {
    // Request location updates
} else {
    // Request permission
    LaunchedEffect(permissionState) {
        permissionState.launchPermissionRequest()
    }
}

//위치 업데이트의 빈도와 정확도를 지정하기 위해 LocationRequest 개체를 생성하고 위치와 함께 호출될 LocationCallback 개체와 함께 FusedLocationProviderClient의 requestLocationUpdates 함수에 전달
//위치 업데이트가 수신되면 Location 개체에서 위도와 경도를 추출하고 이를 사용하여 UI를 업데이트합니다. 위치 업데이트는 백그라운드 스레드에서 수신되므로 UI를 업데이트하려면 LaunchedEffect를 사용해야 합니다.
LaunchedEffect(Unit) {
    val locationRequest = LocationRequest.create().apply {
        interval = 1000
        fastestInterval = 500
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    fusedLocationClient.requestLocationUpdates(
        locationRequest,
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.lastLocation?.let { location ->
                    // Use the location object to get the latitude and longitude
                    val latitude = location.latitude
                    val longitude = location.longitude
                    // Update the UI with the location information
                }
            }
        },
        Looper.getMainLooper()
    )
}
 */



@Preview(showSystemUi = true)
@Composable
fun DefaultPreview() {
    val navController = rememberNavController()
    val onDismissRequest : () -> Unit = {}
    LoveStoryTheme {
        CalendarScreen(navHostController = navController)
    }
    //val singapore = LatLng(1.35, 103.87)
    //val cameraPositionState = rememberCameraPositionState {
    //    position = CameraPosition.fromLatLngZoom(singapore, 10f)
    //}
    //GoogleMap(
    //    modifier = Modifier
    //            .fillMaxSize(),
    //    cameraPositionState = cameraPositionState
    //){
    //    Marker(
    //        state = MarkerState(position = singapore),
    //        title = "Singapore",
    //        snippet = "Marker in Singapore"
    //    )
    //}
}
