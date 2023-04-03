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
import androidx.compose.ui.platform.LocalLifecycleOwner
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
import com.lovestory.lovestory.model.*
import com.lovestory.lovestory.module.getSavedComment
import com.lovestory.lovestory.module.getToken
import com.lovestory.lovestory.module.saveComment
import com.lovestory.lovestory.network.getComment
import com.lovestory.lovestory.network.postComment
import com.lovestory.lovestory.resource.vitro
import com.lovestory.lovestory.ui.components.*
import com.lovestory.lovestory.ui.theme.LoveStoryTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import androidx.lifecycle.lifecycleScope


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

    //var coupleMemoryList by remember { mutableStateOf(generateCoupleMemory()) }

    var coupleMemoryList by remember { mutableStateOf(emptyList<CoupleMemory>()) }
    val stringMemoryList = mutableListOf<StringMemory>()

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleScope = lifecycleOwner.lifecycleScope

    val context = LocalContext.current
    val token = getToken(context)

    var editedcomment by remember { mutableStateOf("") }

    LaunchedEffect(key1 = true) {
        val data = withContext(Dispatchers.IO) {
            getSavedComment(context)
        }
        coupleMemoryList = data
        coupleMemoryList.forEach{CoupleMemory -> Log.d("쉐어드2","$CoupleMemory") }
        saveComment(context, coupleMemoryList) // 이 부분 주석 처리하면 shared preference 초기화 가능

        /*
        val getMemoryList: Response<List<GetMemory>> = getComment(token!!)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        for (getMemory in getMemoryList.body()!!) {
            val date = LocalDate.parse(getMemory.date, formatter)
            val comment = getMemory.comment
            val stringMemory = StringMemory(date.toString(), comment)
            stringMemoryList.add(stringMemory)
        }
        coupleMemoryList = convertToCoupleMemoryList(stringMemoryList)

         */
    }
    Log.d("토큰","$token")

    LaunchedEffect(isPopupVisible){
        if(isPopupVisible){
            if(token != null) {
                val getMemoryList: Response<List<GetMemory>> = getComment(token)

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                for (getMemory in getMemoryList.body()!!) {
                    val date = LocalDate.parse(getMemory.date, formatter)
                    val comment = getMemory.comment
                    val stringMemory = StringMemory(date.toString(), comment)
                    stringMemoryList.add(stringMemory)
                }
                coupleMemoryList = convertToCoupleMemoryList(stringMemoryList)
                //Log.d("코멘트", "${GetMemoryList.body()}")
                coupleMemoryList.forEach{CoupleMemory -> Log.d("업뎃","$CoupleMemory") }
            }
        }
        else{
            saveComment(context, coupleMemoryList)
            val seldate = selection.date
            Log.d("셀렉션","${seldate}, $coupleMemoryList")
            //val post : Response<Any> = postComment(token!!, CoupleMemory(selection.date, editedcomment))
            //Log.d("포스트","${post.body()}")
            editedcomment = ""
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

        //var editedcomment by remember { mutableStateOf("") }
        val existingMemory = coupleMemoryList.firstOrNull { it.date == selection.date }
        if (existingMemory != null) {
            editedcomment = existingMemory.comment
        }

        CalendarDialog(
            selection = selection,
            editedcomment = editedcomment,
            onDismissRequest = {
                if(existingMemory != null) {
                    coupleMemoryList.find{ it.date == selection.date }?.comment = editedcomment

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



@Preview(showSystemUi = true)
@Composable
fun DefaultPreview() {
    val navController = rememberNavController()
    val onDismissRequest : () -> Unit = {}
    LoveStoryTheme {
        CalendarScreen(navHostController = navController)
    }
}
