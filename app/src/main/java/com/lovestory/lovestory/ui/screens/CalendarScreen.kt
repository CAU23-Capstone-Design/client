package com.lovestory.lovestory.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.lovestory.lovestory.resource.vitro
import com.lovestory.lovestory.ui.components.*
import com.lovestory.lovestory.ui.theme.LoveStoryTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(navHostController: NavHostController) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
    val daysOfWeek = remember { daysOfWeek() }

    var selection by remember { mutableStateOf<CalendarDay?>(null)}

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

    val coupleMemoryList = generateCoupleMemory()
    //coupleMemoryList.forEach{coupleMemory -> Log.d("tag","${coupleMemory.date}") }

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
    if (true){//isPopupVisible ) {
        CalendarDialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .width(360.dp)
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = "팝업 창이다...")
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp).height(150.dp).background(color = Color.Gray, RoundedCornerShape(12.dp)))
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp).height(300.dp).background(color = Color.Gray, RoundedCornerShape(12.dp)))
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}



data class couple_memory(val date: LocalDate, val comment: String)

fun generateCoupleMemory(): List<couple_memory> = buildList {
    val currentDate = LocalDate.now()
    add(couple_memory(LocalDate.parse("2023-03-24"), "good"))
    add(couple_memory(currentDate.minusDays(10), "bad"))
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
